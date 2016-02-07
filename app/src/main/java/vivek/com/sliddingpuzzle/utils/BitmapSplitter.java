/*
 * Copyright (c) 2016. Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package vivek.com.sliddingpuzzle.utils;

import android.graphics.Bitmap;

public class BitmapSplitter {

    /**
     *
     * @param image
     * @param width
     * @param row
     * @return
     */
    public static Bitmap[][] split(Bitmap image, int width, int row) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(image, width, width, true);

        //Total number of tiles equals to row*column
        Bitmap[][] tiles = new Bitmap[row][row];

        int imageSize = width/row;
        int xAxis = 0, yAxis = 0;


        for(int i=0; i < row ; i++) {
            xAxis = 0;
            for(int j=0; j < row; j++) {
                tiles[i][j] = Bitmap.createBitmap(scaledBitmap, xAxis, yAxis, imageSize, imageSize);
                xAxis += imageSize;
            }
            yAxis += imageSize;

        }

        return tiles;
    }
}
