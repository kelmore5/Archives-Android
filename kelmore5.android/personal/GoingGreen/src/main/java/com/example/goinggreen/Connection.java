package com.example.goinggreen;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class Connection {
	private static final byte CMD_GET_SENSORS = 2; // () -> (sensors:
	// i32,i32,i32,i32,u16,u16,u16,u16,u16,u16,u16,i16,i16,i16,i16,i16,i16)

	public static BufferedInputStream getSocket(int skip) {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		Iterator<BluetoothDevice> iter = adapter.getBondedDevices().iterator();
		BluetoothDevice device = null;
		while(iter.hasNext()) {
			BluetoothDevice bd = iter.next();
			if(bd.getName().contains("ADK")) {
				device = bd;
			}
		}

		try {
			BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("1dd35050-a437-11e1-b3dd-0800200c9a66"));

			adapter.cancelDiscovery();

			socket.connect();

			getSensors(socket.getOutputStream());
			BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
			input.skip(skip);
			input.mark(1);

			return input;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static int getSensor(BufferedInputStream socket) {
		int sensor = -1;
		try {
			sensor = socket.read();

			socket.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sensor;
	}

	private static void getSensors(OutputStream os) throws IOException {
		sendCommand(os, CMD_GET_SENSORS, CMD_GET_SENSORS, new byte[0], new byte[4]);
	}

	private static byte[] sendCommand(OutputStream os, int command, int sequence, byte[] payload,
									  byte[] buffer) throws IOException {
		int bufferLength = payload.length + 4;
		if (buffer == null || buffer.length < bufferLength) {
			Log.i(ADK.TAG, "allocating new command buffer of length " + bufferLength);
			buffer = new byte[bufferLength];
		}

		buffer[0] = (byte) command;
		buffer[1] = (byte) sequence;
		buffer[2] = (byte) (payload.length & 0xff);
		buffer[3] = (byte) ((payload.length & 0xff00) >> 8);
		if (payload.length > 0) {
			System.arraycopy(payload, 0, buffer, 4, payload.length);
		}
		if (os != null && buffer[1] != -1) {
			try {
				os.write(buffer);
			} catch (IOException e) {
				Log.e(ADK.TAG, "accessory write failed", e);
			}
		}
		return buffer;
	}

	public static void dumpSensors() {
		BufferedInputStream adk = Connection.getSocket(0);
		ProtocolHandler handler = new ProtocolHandler(adk);
		handler.process();
		try {
			adk.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class ProtocolHandler {
		private BufferedInputStream mInputStream;
		//Handler mHandler;
		private boolean gLogPackets;

		public ProtocolHandler(BufferedInputStream inputStream) {
			gLogPackets = true;
			//mHandler = handler;
			mInputStream = inputStream;
		}

		int readByte() throws IOException {
			int retVal = mInputStream.read();
			if (retVal == -1) {
				throw new RuntimeException("End of stream reached.");
			}
			return retVal;
		}

		int readInt16() throws IOException {
			int low = readByte();
			int high = readByte();
			if (gLogPackets) {
				Log.i(ADK.TAG, "readInt16 low=" + low + " high=" + high);
			}
			return low | (high << 8);
		}

		int[] readBuffer(int bufferSize) throws IOException {
			int readBuffer[] = new int[bufferSize];
			int index = 0;
			int bytesToRead = bufferSize;
			while (bytesToRead > 0) {
				readBuffer[index] = mInputStream.read();
				bytesToRead--;
				index++;
				/*int amountRead = mInputStream.read(readBuffer, index,
						bytesToRead);
				if (amountRead == -1) {
					throw new RuntimeException("End of stream reached.");
				}
				bytesToRead -= amountRead;
				index += amountRead;*/
			}
			return readBuffer;
		}

		public void process() {
			try {
				if (gLogPackets)
					Log.i(ADK.TAG, "about to read opcode");
				int opCode = readByte();
				if (gLogPackets)
					Log.i(ADK.TAG, "opCode = " + opCode);
				if (isValidOpCode(opCode)) {
					int sequence = readByte();
					if (gLogPackets)
						Log.i(ADK.TAG, "sequence = " + sequence);
					int replySize = readInt16();
					if (gLogPackets)
						Log.i(ADK.TAG, "replySize = " + replySize);
					int[] replyBuffer = readBuffer(replySize);
					if (gLogPackets) {
						Log.i(ADK.TAG,
								"replyBuffer: "
										+ Utilities.dumpBytes(replyBuffer,
										replyBuffer.length)); //
					}
				}
			} catch (IOException e) {
				Log.i(ADK.TAG, "ProtocolHandler error " + e.toString());
			}
		}

		boolean isValidOpCode(int opCodeWithReplyBitSet) {
			if ((opCodeWithReplyBitSet & 0x80) != 0) {
				int opCode = opCodeWithReplyBitSet & 0x7f;
				return ((opCode >= 1) && (opCode <= 16));
			}
			return false;
		}
	}
}