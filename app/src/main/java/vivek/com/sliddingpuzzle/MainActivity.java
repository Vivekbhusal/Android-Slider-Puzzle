/*
 * Copyright (c) 2016. Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *                            The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *                            THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package vivek.com.sliddingpuzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import vivek.com.sliddingpuzzle.model.Position;
import vivek.com.sliddingpuzzle.model.TileItem;
import vivek.com.sliddingpuzzle.utils.BitmapSplitter;
import vivek.com.sliddingpuzzle.utils.DeviceProperty;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{

    RelativeLayout fullBoardView;
    Bitmap[][] bitmapTiles;
    LinkedHashMap<Integer, TileItem> puzzleItemList;
    LinkedHashMap<Integer, TileItem> shuffledTiles;
    TileItem emptyTile;
    Button ViewOriginalImage;
    ImageView originImage;
    RectF moveableBoundary;
    TextView stepCountView;

    Point deviceDimension;
//    int puzzleBoardWidth;
    int touchPositionX, touchPositionY;

    public static int boardWidth;
    public static int numberOfRows = 4;
    public static int PUZZLE_BOARD_LEFT_MARGIN = 20;
    private int tileSize;
    private int stepCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the dimension of the device
        deviceDimension = DeviceProperty.getDeviceDimension(MainActivity.this);

        //Get the width of puzzle board leaving equal margin on left and right
        boardWidth = deviceDimension.x - 2*PUZZLE_BOARD_LEFT_MARGIN;
        tileSize = boardWidth/numberOfRows;

        //View to display the number of counts
        stepCountView = (TextView) findViewById(R.id.puzzleStepCounts);

        //Get the Puzzle board and initialize its parameter
        fullBoardView = (RelativeLayout) findViewById(R.id.puzzleFullBoardView);
        LinearLayout.LayoutParams boardParam = new LinearLayout.LayoutParams(boardWidth,boardWidth);
        boardParam.leftMargin = PUZZLE_BOARD_LEFT_MARGIN;
        boardParam.rightMargin = PUZZLE_BOARD_LEFT_MARGIN;
        fullBoardView.setLayoutParams(boardParam);

        //Initialize the original image view and set it on same arrangment for good display
        originImage = (ImageView) findViewById(R.id.originalImage);
        originImage.setLayoutParams(boardParam);

        //Initialize button to view Original image
        ViewOriginalImage = (Button) findViewById(R.id.originalImageButton);
        ViewOriginalImage.setOnTouchListener(this);

        //Create the sliced bitmap from provided Image
        bitmapTiles = this.createTileBitmaps();

        //Initialize the list of puzzle tiles with sliced bitmaps
        puzzleItemList = this.initializePuzzleTiles(bitmapTiles);

        //Shuffle the tile and render on play board
        this.shuffleAndRenderTiles(puzzleItemList);
    }

    /**
     * Creates the array of bitmap by splitting image into pieces
     * @return
     */
    private Bitmap[][] createTileBitmaps() {
        Bitmap image = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.imagepuzzle2);
        return BitmapSplitter.split(image, boardWidth, numberOfRows);
    }

    /**
     * Shuffle the existing list of tiles to render randomly
     * @param puzzleTile
     * @return
     */
    private LinkedHashMap<Integer, TileItem> shuffleTiles(LinkedHashMap<Integer, TileItem> puzzleTile) {
        //Remove the list piece
        emptyTile = puzzleTile.get(puzzleTile.size()-1);
        emptyTile.setImage(null);
        emptyTile.setIsBlank(true);

        //shuffle
        List keys = new ArrayList(puzzleTile.keySet());
        Collections.shuffle(keys);

        LinkedHashMap<Integer, TileItem> shuffledTile = new LinkedHashMap<>();
        int i=0;
        for (Object o: keys){
            TileItem item = puzzleTile.get(o);
            int xAxis = (i < numberOfRows)? i : (i % numberOfRows);
            int yAxis = 0;
            if(i>=4 && i<=7) {
                yAxis = 1;
            } else if(i>=8 && i<=11) {
                yAxis = 2;
            } else if(i>=12 && i<=15) {
                yAxis = 3;
            }

            item.setCurrentPosition(new Position(xAxis, yAxis));
            shuffledTile.put((int) o, puzzleTile.get(o));
            i++;
        }
        return shuffledTile;
    }

    /**
     * Creates the array of Tile item, assigns bitmap and create Hashmap
     * @param bitmapTiles
     * @return
     */
    private LinkedHashMap<Integer, TileItem> initializePuzzleTiles(Bitmap[][] bitmapTiles) {
        LinkedHashMap<Integer, TileItem> puzzleItem = new LinkedHashMap<>();
        int tileWidth = (boardWidth/numberOfRows);
        int bitmapPosition = 0;

        for (int i = 0; i < numberOfRows; i++) {
            for(int j = 0; j< numberOfRows; j++) {
                TileItem tile = new TileItem(getApplicationContext());
                tile.setId(bitmapPosition);
                tile.setStartingPosition(new Position(i, j));
                tile.setImage(bitmapTiles[i][j]);
                tile.setDimension(tileWidth);
                tile.setOnTouchListener(this);
                puzzleItem.put(bitmapPosition++, tile);
            }
        }
        return puzzleItem;
    }


    /**
     * Shuffle the origin list of tiles before render.
     *
     * @param puzzleItem
     */
    private void shuffleAndRenderTiles(LinkedHashMap<Integer, TileItem> puzzleItem) {
        shuffledTiles = this.shuffleTiles(puzzleItem);

        for(Map.Entry<Integer, TileItem> entry: shuffledTiles.entrySet()) {
            TileItem item = entry.getValue();
            fullBoardView.addView(item, item.setLayout());
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.originalImageButton) {
            displayOriginalImage(event);
            return true;
        }

        TileItem selectedTile = (TileItem) v;

        //Do nothing if the tile is blank
        if(selectedTile.getIsBlank()) {
            return false;
        }

        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if(!checkIfValidMove(selectedTile))
                    return false;
                touchPositionX = (int) event.getRawX();
                touchPositionY = (int) event.getRawY();
                selectedTile.bringToFront();
                moveableBoundary = getMoveableBoundry(selectedTile);
                break;
            case MotionEvent.ACTION_MOVE:
                dragTilesAround(selectedTile, event);
                touchPositionX = (int) event.getRawX();
                touchPositionY = (int) event.getRawY();

                break;
            case MotionEvent.ACTION_UP:
                if(tileDraggedMoreThenHalfWay(selectedTile) || isJustClick(selectedTile)) {
                    swapTileWithEmpty(selectedTile);
                    increaseStepCounts();
                } else {
                    bringTileToOriginalPostion(selectedTile);
                }

                break;
        }
        return true;
    }

    /**
     * Check if the tile can make a move, that is,
     * if the tile has blank space to its surrounding
     * @param selectedItem
     * @return
     */
    public boolean checkIfValidMove(TileItem selectedItem) {

        return (selectedItem.isAboveOf(emptyTile)
                || selectedItem.isbelowOf(emptyTile)
                || selectedItem.isLeftOf(emptyTile)
                || selectedItem.isRightOf(emptyTile));
    }

    /**
     * Swap the position of tile and empty space
     * @param selectedItem
     */
    public void swapTileWithEmpty(TileItem selectedItem) {
        Position selectedItemPosition = selectedItem.getCurrentPosition();
//
//        ObjectAnimator animator = ObjectAnimator.ofObject(selectedItem, "",
//                new FloatEvaluator(), )
        selectedItem.swapPositionWith(emptyTile.getCurrentPosition());
        emptyTile.swapPositionWith(selectedItemPosition);

    }


    /**
     * In case the swap fails or is illegal move,
     * we bring the selected tile to its original postion
     * @param selectedItem
     */
    public void bringTileToOriginalPostion(TileItem selectedItem) {
        selectedItem.setLayoutParams(selectedItem.setLayout());
    }

    /**
     * Check if the tile has move halfway to the empty tiles
     * @param selectedItem
     * @return
     */
    private boolean tileDraggedMoreThenHalfWay(TileItem selectedItem) {

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) selectedItem.getLayoutParams();
        int originalMargin = 0, currentMargin = 0;

        if(selectedItem.isbelowOf(emptyTile) || selectedItem.isAboveOf(emptyTile)) {
            originalMargin = selectedItem.getCurrentPosition().getyAxis() * tileSize;
            currentMargin = params.topMargin;
        } else {
            originalMargin = selectedItem.getCurrentPosition().getxAxis() * tileSize;
            currentMargin = params.leftMargin;
        }

        if(Math.abs(originalMargin - currentMargin) >= tileSize/2) {
            return true;
        }
        return false;
    }

    /**
     * Check if the touch even was just click
     * @param selectedItem
     * @return
     */
    private boolean isJustClick(TileItem selectedItem) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) selectedItem.getLayoutParams();
        int originalTopMargin = selectedItem.getCurrentPosition().getyAxis() * tileSize;
        int currentTopMargin = params.topMargin;

        int originLeftMargin = selectedItem.getCurrentPosition().getxAxis() * tileSize;
        int currentLeftMargin = params.leftMargin;

        /**
         * Many times clicking move ths side, so to add the better experience
         * we keep 5pixels plus minus.
         */
        return (Math.abs(originalTopMargin - currentTopMargin) < 5
                && (originLeftMargin - currentLeftMargin) < 5);
    }

    /**
     * Display the original image during button press and hide after release
     * @param event
     */
    public void displayOriginalImage(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                fullBoardView.setVisibility(View.GONE);
                originImage.setVisibility(View.VISIBLE);
                break;
            case MotionEvent.ACTION_UP:
                fullBoardView.setVisibility(View.VISIBLE);
                originImage.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Dragging the selected tiles
     * @param selectedTile
     * @param event
     */
    public void dragTilesAround(TileItem selectedTile, MotionEvent event) {
        int xCoordinate = (int) event.getRawX() - touchPositionX;
        int yCoordinate = (int) event.getRawY() - touchPositionY;

        //Get the current boundary of selected tile.
        RectF selectedTileBoundary = getSelectedTileBoundary(selectedTile);

        /**
         * on coordinate might go side ways duing drag, so if its not within the
         * moveableBoundary, do not render the layout
         */
        if(moveableBoundary.contains(selectedTileBoundary)) {

            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) selectedTile.getLayoutParams();
            param.height = tileSize;
            param.width = tileSize;
            if(selectedTile.isRightOf(emptyTile) || selectedTile.isLeftOf(emptyTile)) {
                param.leftMargin = xCoordinate + param.leftMargin;
            } else {
                param.topMargin = yCoordinate + param.topMargin;
            }
            selectedTile.setLayoutParams(param);
        }

    }

    /**
     * Calculate the total boundary on where slide can move
     * summation of selected slide and empty slide
     * @param selectedItem
     * @return
     */
    public RectF getMoveableBoundry(TileItem selectedItem) {
        int boardTop = (int) Math.floor(fullBoardView.getY());
        int boardLeft = (int) Math.floor(fullBoardView.getX());

        int emptyTop = (emptyTile.getCurrentPosition().getyAxis() * tileSize)+boardTop;
        int emptyLeft = (emptyTile.getCurrentPosition().getxAxis() * tileSize)+boardLeft;
        int emptyRght = emptyLeft + tileSize;
        int emptyButtom = emptyTop +tileSize;

        int selectedItemTop = (selectedItem.getCurrentPosition().getyAxis() * tileSize)+boardTop;
        int selectedItemLeft = (selectedItem.getCurrentPosition().getxAxis() * tileSize)+boardLeft;
        int selectedItemRght = selectedItemLeft + tileSize;
        int selectedItemButtom = selectedItemTop + tileSize;

        int left = (emptyLeft <= selectedItemLeft) ? emptyLeft: selectedItemLeft;
        int top = (emptyTop <= selectedItemTop) ? emptyTop: selectedItemTop;
        int right = (emptyRght >= selectedItemRght) ? emptyRght : selectedItemRght;
        int buttom = (emptyButtom >= selectedItemButtom) ? emptyButtom : selectedItemButtom;

        return new RectF(left, top, right, buttom);
    }

    /**
     * Calculate the total boundary of selected Slide at current position
     * Used to measure the boundary when slide is being dragged
     * @param selectedItem
     * @return
     */
    public RectF getSelectedTileBoundary(TileItem selectedItem) {
        int boardTop = (int) Math.floor(fullBoardView.getY());
        int boardLeft = (int) Math.floor(fullBoardView.getX());

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) selectedItem.getLayoutParams();
        int selectedItemLeft = boardLeft + params.leftMargin;
        int selectedItemTop = boardTop + params.topMargin;
        int selectedItemRght = selectedItemLeft + tileSize;
        int selectedItemButtom = selectedItemTop + tileSize;

        return new RectF(selectedItemLeft, selectedItemTop, selectedItemRght, selectedItemButtom);
    }

    public void increaseStepCounts() {
        stepCount++;
        stepCountView.setText(String.valueOf(stepCount));
    }
}
