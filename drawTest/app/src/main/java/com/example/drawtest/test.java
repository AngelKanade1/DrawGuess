package com.example.drawtest;

import org.json.JSONException;
import org.json.JSONObject;

public class test {
    public static void main(String[] args) {
        JSONObject playerInfo;

        String[] playersIndex;

        int round;
        String infoString = "{'index':'A,B,C,D,E,F','A': {'question': 'wordA', '1bitmap': 'A1Bitmap', '1guess': 'F1BitmapA2Gues1', '2bitmap': 'E1BitmapF1GuessA2Bitmap', '2guess': 'D1BitmapE1GuessF2BitmapA2Guess', '3bitmap': 'C1BitmapD1GuessE2BitmapF2GuessA3Bitmap', '3guess': 'B1BitmapC1GuessD2BitmapE2GuessF2BitmapA3Guess'}, 'B': {'question': 'wordB', '1bitmap': 'B1Bitmap', '1guess': 'A1BitmapB1Guess', '2bitmap': 'F1BitmapA2Gues1B2Bitmao', '2guess': 'E1BitmapF1GuessA2BitmapB2Guess', '3bitmap': 'D1BitmapE1GuessF2BitmapA2GuessB3Bitmap', '3guess': 'C1BitmapD1GuessE2BitmapF2GuessA3BitmapB3Guess'}, 'C': {'question': 'wordC', '1bitmap': 'C1Bitmap', '1guess': 'B1BitmapC1Guess', '2bitmap': 'A1BitmapB1GuessC2Bitmap', '2guess': 'F1BitmapA2Gues1B2BitmaoC2Guess', '3bitmap': 'E1BitmapF1GuessA2BitmapB2GuessC3Bitmap', '3guess': 'D1BitmapE1GuessF2BitmapA2GuessB3BitmapC3Guess'}, 'D': {'question': 'wordD', '1bitmap': 'D1Bitmap', '1guess': 'C1BitmapD1Guess', '2bitmap': 'B1BitmapC1GuessD2Bitmap', '2guess': 'A1BitmapB1GuessC2BitmapD2Guess', '3bitmap': 'F1BitmapA2Gues1B2BitmaoC2GuessD3Bitmap', '3guess': 'E1BitmapF1GuessA2BitmapB2GuessC3BitmapD3Guess'}, 'E': {'question': 'wordE', '1bitmap': 'E1Bitmap', '1guess': 'D1BitmapE1Guess', '2bitmap': 'C1BitmapD1GuessE2Bitmap', '2guess': 'B1BitmapC1GuessD2BitmapE2Guess', '3bitmap': 'A1BitmapB1GuessC2BitmapD2GuessE3Bitmap', '3guess': 'F1BitmapA2Gues1B2BitmaoC2GuessD3BitmapE3Guess'}, 'F': {'question': 'wordF', '1bitmap': 'F1Bitmap', '1guess': 'E1BitmapF1Guess', '2bitmap': 'D1BitmapE1GuessF2Bitmap', '2guess': 'C1BitmapD1GuessE2BitmapF2Guess', '3bitmap': 'B1BitmapC1GuessD2BitmapE2GuessF2Bitmap', '3guess': 'A1BitmapB1GuessC2BitmapD2GuessE3BitmapF3Guess'}}";
        try {
            playerInfo = convertToJson(infoString);
            playersIndex = playerInfo.getString("index").split(",");
            int len = playersIndex.length;
            int temp = 0;
            round = playersIndex.length/2;
            String[][] show = new String[len][round*2+1]; //0:3,5 1:4,6 2:1,3 3:2,4
            for (String index:playersIndex){ //(01)(32)(23)(14) (11)(02)(33)(24) (21)(12)(03)(34) (31)(22)(13)(04)
                JSONObject playerJson = (JSONObject) playerInfo.get(index);
                show[temp][0]=playerJson.getString("question");
                for (int r = 1;r<=round;r++){
                    if (temp-(2*r-1)+1<0){
                        show[temp-(2*r-1)+len+1][r*2-1]=playerJson.getString(r+"bitmap");
                    }else{
                        show[temp-(2*r-1)+1][r*2-1]=playerJson.getString(r+"bitmap");
                    }
                    if (temp-(2*r-1)<0){
                        show[len-(2*r-1)+temp][r*2] = playerJson.getString(r+"guess");
                    }
                    else{
                        show[temp-(2*r-1)][r*2]=playerJson.getString(r+"guess");
                    }
                }
                temp ++;
            }
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
        String cleanedInput = input.replace("'", "\"");
        return new JSONObject(cleanedInput);
    }
}