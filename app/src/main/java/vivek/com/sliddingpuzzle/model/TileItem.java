/*
 * Copyright (c) 2016. Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package vivek.com.sliddingpuzzle.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import vivek.com.sliddingpuzzle.R;

public class TileItem extends ImageView {

    private Position startingPosition;
    private Position currentPosition;

    Boolean isBlank = false;

    public TileItem(Context context ) {
        super(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(5, 5, 5, 5);
        setLayoutParams(params);
//        setOnTouchListener(this);

    }

    public void setImage(Bitmap image) {
        if(image == null) {
            setBackgroundColor(getContext().getResources().getColor(R.color.black));
            setAlpha(100);
            isBlank = true;
        } else {
            setImageBitmap(image);
        }

    }


    public Position getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(Position startingPosition) {
        this.startingPosition = startingPosition;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setDimension(int width) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        setLayoutParams(params);
    }

    @Override
    public String toString() {
        return "TileItem{" +
                ", isBlank=" + isBlank +
                '}';
    }
}
