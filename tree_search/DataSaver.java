package tree_search;

import general.DataArray;
import general.Util;
import general.Values;

public class DataSaver {

	public static int savingIndex = -1;

	public static int save(DataArray[] dta) {
		savingIndex++;
		String address = Values.MEMORY_LOCATION + Values.PREVIOUS_NAME_FOR_TEMP_CNN + savingIndex + ".txt";
		Util.serialize(address, dta);
		return savingIndex;
	}

	public static DataArray[] load(int index) {
		String address = Values.MEMORY_LOCATION + Values.PREVIOUS_NAME_FOR_TEMP_CNN + index + ".txt";

		DataArray[] dtA = (DataArray[]) Util.deserialize(address);

		return dtA;
	}

	public static void end() {
		savingIndex = -1;

		Util.deleteAllFilesThatBeginsWith(Values.PREVIOUS_NAME_FOR_TEMP_CNN);
	}

}
