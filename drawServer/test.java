import org.json.JSONException;
import org.json.JSONObject;

public class test {
    public static void main(String[] args) {
        try {
            String infoString = "{'index':'A,B,C,D','A': {'question': 'wordA', '1bitmap': 'A1bitmap', '1guess': 'A1guess', '2bitmap': 'D1bitmap', '2guess': 'D1bitmap'}, 'B': {'question': 'wordB', '1bitmap': 'B1bitmap', '1guess': 'A1guess', '2bitmap': 'D1bitmap', '2guess': 'D1bitmap'}, \n" +
                    "'C': {'question': 'wordC', '1bitmap': 'C1bitmap', '1guess': 'A1guess', '2bitmap': 'D1bitmap', '2guess': 'D1bitmap'}, 'D': {'question': 'wordD', '1bitmap': 'D1bitmap', '1guess': 'A1guess', '2bitmap': 'D1bitmap', '2guess': 'D1bitmap'}}  ";

            JSONObject playerInfo = convertToJson(infoString);
            String[] playersIndex = playerInfo.getString("index").split(",");
            int len = playersIndex.length;
            int temp = 0;
            int round = playersIndex.length / 2;
            String[][] show = new String[len][round * 2 + 1];
            for (String index : playersIndex) {
                JSONObject playerJson = convertToJson(playerInfo.getString(index));
                show[temp][0] = playerJson.getString("question");
                for (int r = 1; r <= round; r++) {
                    show[temp][r] = playerJson.getString(r + "bitmap");
                    if (temp - 1 < 0) {
                        show[show.length - 1][r] = playerJson.getString(r + "guess");
                    } else {
                        show[temp - 1][r] = playerJson.getString(r + "guess");
                    }
                }
                temp++;
            }

            // 输出 show 数组的值
            for (String[] row : show) {
                for (String value : row) {
                    System.out.print(value + "\t");
                }
                System.out.println();
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static JSONObject convertToJson(String input) throws JSONException {
        // 去除字符串中的单引号
        String cleanedInput = input.replace("'", "\"");

        return new JSONObject(cleanedInput);
    }
}