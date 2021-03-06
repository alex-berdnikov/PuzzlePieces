package com.example.alexberdnikov.puzzlepieces.view.jigsaw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Size;
import com.example.alexberdnikov.puzzlepieces.BuildConfig;
import com.example.alexberdnikov.puzzlepieces.view.Piece;
import com.example.alexberdnikov.puzzlepieces.view.PiecesPicker;
import com.example.alexberdnikov.puzzlepieces.view.Puzzle;

public class JigsawPuzzle extends Puzzle {
  private final boolean DRAW_NUMBERS_ON_PIECES = true;
  private int pieceSquareWidth;
  private int pieceSquareHeight;

  private PiecesGenerator piecesGenerator;
  private Paint piecePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  public JigsawPuzzle(Context context, int columnsCount, int rowsCount) {
    super(context);
    piecesGenerator = new PiecesGenerator(columnsCount, rowsCount);
    definePieceSquareSize();
  }

  private void definePieceSquareSize() {
    pieceSquareWidth = getPuzzleAreaSize().getWidth() / piecesGenerator.getPuzzleColumnsCount();
    pieceSquareHeight = getPuzzleAreaSize().getHeight() / piecesGenerator.getPuzzleRowsCount();
  }

  @Override protected Piece createPiece(int number, int x, int y) {
    return new JigsawPiece(
        createPieceImage(number),
        piecesGenerator.getSidesDescription(number),
        piecesGenerator.getPuzzleColumnsCount(),
        piecesGenerator.getPuzzleRowsCount(),
        number,
        x,
        y);
  }

  private Bitmap createPieceImage(int pieceNumber) {
    Path piecePath = createPiecePath(pieceNumber);
    Size pieceSize = calculatePieceBitmapSize(pieceNumber);

    Bitmap cutPieceBitmap =
        Bitmap.createBitmap(pieceSize.getWidth(), pieceSize.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas pieceCanvas = new Canvas(cutPieceBitmap);

    piecePaint.setColor(0xFF000000);
    piecePaint.setStrokeWidth(1);
    piecePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    pieceCanvas.drawPath(piecePath, piecePaint);
    piecePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    Point pieceImageSpriteCoordinates = calculatePieceImageCoordinates(pieceNumber);

    Bitmap pieceBitmap = Bitmap.createBitmap(
        getImageBitmap(),
        pieceImageSpriteCoordinates.x,
        pieceImageSpriteCoordinates.y,
        pieceSize.getWidth(),
        pieceSize.getHeight());

    pieceCanvas.drawBitmap(pieceBitmap, 0, 0, piecePaint);
    piecePaint.setXfermode(null);
    drawPeaceNumberIfNeeded(pieceCanvas, pieceSize, pieceNumber);

    piecePaint.setColor(Color.BLACK);
    piecePaint.setStyle(Paint.Style.STROKE);
    pieceCanvas.drawPath(piecePath, piecePaint);

    return cutPieceBitmap;
  }

  private void drawPeaceNumberIfNeeded(Canvas pieceCanvas, Size pieceSize, int pieceNumber) {
    if (DRAW_NUMBERS_ON_PIECES && BuildConfig.DEBUG) {
      piecePaint.setAntiAlias(true);
      piecePaint.setColor(Color.WHITE);
      piecePaint.setTextSize(34);
      piecePaint.setStyle(Paint.Style.FILL);
      pieceCanvas.drawText(Integer.toString(pieceNumber),
          pieceSize.getWidth() / 2f - 20, pieceSize.getHeight() / 2f, piecePaint);
    }
  }

  @Override protected PiecesPicker createPiecesPicker(int screenWidth, int screenHeight) {
    return new JigsawPiecesPicker(getPieces(), screenWidth, screenHeight);
  }

  @Override public void generate() {
    for (int i = 0; i < piecesGenerator.getPiecesCount(); i++) {
      // Just put the pieces consequently
      getPieces().add(createPiece(i, (i % 16) * (96 + 18) + 40, (i / 16) * (96 + 18) + 40));
    }
    onGenerated();
  }

  private Size calculatePieceBitmapSize(int pieceNumber) {
    JigsawPiece.SidesDescription sidesDescription =
        piecesGenerator.getSidesDescription(pieceNumber);
    int pieceWidth = pieceSquareWidth;
    int pieceHeight = pieceSquareHeight;

    if (sidesDescription.getSideForm(JigsawPiece.SidesDescription.SIDE_TOP)
        == JigsawPiece.SidesDescription.SIDE_FORM_CONVEX) {
      pieceHeight += JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT;
    }

    if (sidesDescription.getSideForm(JigsawPiece.SidesDescription.SIDE_BOTTOM)
        == JigsawPiece.SidesDescription.SIDE_FORM_CONVEX) {
      pieceHeight += JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT;
    }

    if (sidesDescription.getSideForm(JigsawPiece.SidesDescription.SIDE_LEFT)
        == JigsawPiece.SidesDescription.SIDE_FORM_CONVEX) {
      pieceWidth += JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT;
    }

    if (sidesDescription.getSideForm(JigsawPiece.SidesDescription.SIDE_RIGHT)
        == JigsawPiece.SidesDescription.SIDE_FORM_CONVEX) {
      pieceWidth += JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT;
    }

    return new Size(pieceWidth, pieceHeight);
  }

  private Point calculatePieceImageCoordinates(int pieceNumber) {
    JigsawPiece.SidesDescription sidesDescription =
        piecesGenerator.getSidesDescription(pieceNumber);

    int pieceNumberInRow = pieceNumber % piecesGenerator.getPuzzleColumnsCount();
    int imageLeft = pieceSquareWidth * pieceNumberInRow;
    if (pieceNumberInRow != 0
        && sidesDescription.getSideForm(JigsawPiece.SidesDescription.SIDE_LEFT)
        == JigsawPiece.SidesDescription.SIDE_FORM_CONVEX) {
      imageLeft -= JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT;
    }

    int pieceNumberInColumn = pieceNumber / piecesGenerator.getPuzzleColumnsCount();
    int imageTop = pieceSquareHeight * pieceNumberInColumn;
    if (pieceNumberInColumn != 0
        && sidesDescription.getSideForm(JigsawPiece.SidesDescription.SIDE_TOP)
        == JigsawPiece.SidesDescription.SIDE_FORM_CONVEX) {
      imageTop -= JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT;
    }

    return new Point(imageLeft, imageTop);
  }

  private Path createPiecePath(int pieceNumber) {
    Path pieceFormPath = new Path();
    final int START_X = 0;
    final int START_Y = 1;

    int[] pathStartPoint = setPathStartPoint(pieceFormPath, pieceNumber);
    pieceFormPath.moveTo(pathStartPoint[START_X], pathStartPoint[START_Y]);
    drawTopSide(pieceFormPath, pieceNumber, pathStartPoint[START_X], pathStartPoint[START_Y]);
    drawRightSide(pieceFormPath, pieceNumber, pathStartPoint[START_X], pathStartPoint[START_Y]);
    drawBottomSide(pieceFormPath, pieceNumber, pathStartPoint[START_X], pathStartPoint[START_Y]);
    drawLeftSide(pieceFormPath, pieceNumber, pathStartPoint[START_X], pathStartPoint[START_Y]);
    pieceFormPath.close();

    return pieceFormPath;
  }

  private int[] setPathStartPoint(Path path, int pieceNumber) {
    final int leftSide = piecesGenerator.getSidesDescription(pieceNumber)
        .getSideForm(JigsawPiece.SidesDescription.SIDE_LEFT);
    final int startX;
    switch (leftSide) {
      case JigsawPiece.SidesDescription.SIDE_FORM_FLAT:
        startX = 0;
        break;
      case JigsawPiece.SidesDescription.SIDE_FORM_CONCAVE:
        startX = 0;
        break;
      case JigsawPiece.SidesDescription.SIDE_FORM_CONVEX:
        startX = JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT;
        break;
      default:
        throw new IllegalStateException("SidesDescription side form has undefined value.");
    }

    final int topSideForm = piecesGenerator.getSidesDescription(pieceNumber)
        .getSideForm(JigsawPiece.SidesDescription.SIDE_TOP);
    final int startY;
    switch (topSideForm) {
      case JigsawPiece.SidesDescription.SIDE_FORM_FLAT:
        startY = 0;
        break;
      case JigsawPiece.SidesDescription.SIDE_FORM_CONCAVE:
        startY = 0;
        break;
      case JigsawPiece.SidesDescription.SIDE_FORM_CONVEX:
        startY = JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT;
        break;
      default:
        throw new IllegalStateException("SidesDescription side form has undefined value.");
    }

    path.moveTo(startX, startY);
    return new int[] { startX, startY };
  }

  /**
   * Draws top side of a piece. Must be called first in a series of methods drawing piece sides.
   *
   * @param path path to draw
   */
  private void drawTopSide(Path path, int pieceNumber, int startX, int startY) {
    final int TOP_SIDE_FORM = piecesGenerator.getSidesDescription(pieceNumber)
        .getSideForm(JigsawPiece.SidesDescription.SIDE_TOP);
    switch (TOP_SIDE_FORM) {
      case JigsawPiece.SidesDescription.SIDE_FORM_FLAT:
        path.lineTo(startX + pieceSquareWidth, startY);
        break;
      case JigsawPiece.SidesDescription.SIDE_FORM_CONCAVE:
        path.lineTo(startX + pieceSquareWidth / 3f, startY);
        path.cubicTo(
            startX + (pieceSquareWidth / 3f) - JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,
            startY + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,

            startX + ((pieceSquareWidth / 3f) * 2) + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,
            startY + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,

            startX + ((pieceSquareWidth / 3f) * 2), startY);
        path.lineTo(startX + pieceSquareWidth, startY);
        break;
      case JigsawPiece.SidesDescription.SIDE_FORM_CONVEX:
        path.lineTo(startX + pieceSquareWidth / 3f, startY);
        path.cubicTo(
            startX + (pieceSquareWidth / 3f) - JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            0,

            startX + ((pieceSquareWidth / 3) * 2) + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            0,

            startX + ((pieceSquareWidth / 3f) * 2), startY);
        path.lineTo(startX + pieceSquareWidth, startY);
        break;
    }
  }

  /**
   * Must be called after {@link #drawTopSide(Path, int, int, int)} and before
   * {@link #drawBottomSide(Path, int, int, int)}.
   */
  private void drawRightSide(Path path, int pieceNumber, int startX, int startY) {
    final int RIGHT_SIDE = piecesGenerator.getSidesDescription(pieceNumber)
        .getSideForm(JigsawPiece.SidesDescription.SIDE_RIGHT);
    switch (RIGHT_SIDE) {
      case JigsawPiece.SidesDescription.SIDE_FORM_FLAT:
        path.lineTo(startX + pieceSquareWidth, startY + pieceSquareHeight);
        break;
      case JigsawPiece.SidesDescription.SIDE_FORM_CONCAVE:
        path.lineTo(startX + pieceSquareWidth, startY + pieceSquareHeight / 3f);
        path.cubicTo(
            startX + pieceSquareWidth - JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + (pieceSquareHeight / 3f) - JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX + pieceSquareWidth - JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + (pieceSquareHeight / 3) * 2 + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX + pieceSquareWidth,
            startY + (pieceSquareHeight / 3f) * 2);
        path.lineTo(startX + pieceSquareWidth, startY + pieceSquareHeight);
        break;
      case JigsawPiece.SidesDescription.SIDE_FORM_CONVEX:
        path.lineTo(startX + pieceSquareWidth, startY + pieceSquareHeight / 3f);
        path.cubicTo(
            startX + pieceSquareWidth + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + (pieceSquareHeight / 3f) - JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX + pieceSquareWidth + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + (pieceSquareHeight / 3) * 2 + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX + pieceSquareWidth,
            startY + (pieceSquareHeight / 3f) * 2);
        path.lineTo(startX + pieceSquareWidth, startY + pieceSquareHeight);
        break;
    }
  }

  /**
   * Must be called after {@link #drawRightSide(Path, int, int, int)} and before
   * {@link #drawLeftSide(Path, int, int, int)}.
   */
  private void drawBottomSide(Path path, int pieceNumber, int startX, int startY) {
    final int BOTTOM_SIDE = piecesGenerator.getSidesDescription(pieceNumber)
        .getSideForm(JigsawPiece.SidesDescription.SIDE_BOTTOM);
    switch (BOTTOM_SIDE) {
      case JigsawPiece.SidesDescription.SIDE_FORM_FLAT:
        path.lineTo(startX, startY + pieceSquareHeight);
        break;
      case JigsawPiece.SidesDescription.SIDE_FORM_CONCAVE:
        path.lineTo(startX + (pieceSquareWidth / 3f) * 2, startY + pieceSquareHeight);
        path.cubicTo(
            startX + ((pieceSquareWidth / 3) * 2) + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,
            startY + pieceSquareHeight - JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,

            startX + (pieceSquareWidth / 3f) - JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,
            startY + pieceSquareHeight - JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,

            startX + pieceSquareWidth / 3f,
            startY + pieceSquareHeight);
        path.lineTo(startX, startY + pieceSquareHeight);
        break;
      case JigsawPiece.SidesDescription.SIDE_FORM_CONVEX:
        path.lineTo(startX + (pieceSquareWidth / 3f) * 2, startY + pieceSquareHeight);
        path.cubicTo(
            startX + ((pieceSquareWidth / 3) * 2) + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,
            startY + pieceSquareHeight + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,

            startX + (pieceSquareWidth / 3f) - JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,
            startY + pieceSquareHeight + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,

            startX + pieceSquareWidth / 3f,
            startY + pieceSquareHeight);
        path.lineTo(startX, startY + pieceSquareHeight);
        break;
    }
  }

  /**
   * Must be called last, after {@link #drawBottomSide(Path, int, int, int)},
   * in a series of methods drawing piece sides.
   */
  private void drawLeftSide(Path path, int pieceNumber, int startX, int startY) {
    final int LEFT_SIDE = piecesGenerator.getSidesDescription(pieceNumber)
        .getSideForm(JigsawPiece.SidesDescription.SIDE_LEFT);
    switch (LEFT_SIDE) {
      case JigsawPiece.SidesDescription.SIDE_FORM_FLAT:
        path.lineTo(startX, startY);
        break;
      case JigsawPiece.SidesDescription.SIDE_FORM_CONCAVE:
        path.lineTo(startX, startY + (pieceSquareHeight / 3f) * 2);
        path.cubicTo(
            startX + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + ((pieceSquareHeight / 3) * 2) + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + (pieceSquareHeight / 3f) - JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX,
            startY + (pieceSquareHeight / 3f));
        path.lineTo(startX, startY);
        break;
      case JigsawPiece.SidesDescription.SIDE_FORM_CONVEX:
        path.lineTo(startX, startY + (pieceSquareHeight / 3f) * 2);
        path.cubicTo(
            startX - JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + ((pieceSquareHeight / 3) * 2) + JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX - JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + (pieceSquareHeight / 3f) - JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX,
            startY + (pieceSquareHeight / 3f));
        path.lineTo(startX, startY);
        break;
    }
  }
}
