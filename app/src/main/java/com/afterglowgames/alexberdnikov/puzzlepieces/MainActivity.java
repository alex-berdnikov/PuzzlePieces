package com.afterglowgames.alexberdnikov.puzzlepieces;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.afterglowgames.alexberdnikov.puzzlepieces.view.PuzzleView;
import com.afterglowgames.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPuzzle;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.puzzle_view) PuzzleView puzzleView;

  @Override public void onCreate(@Nullable Bundle savedInstanceState,
      @Nullable PersistableBundle persistentState) {
    super.onCreate(savedInstanceState, persistentState);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
  }

  @Override protected void onResume() {
    super.onResume();
    setupUi();
  }

  private void setupUi() {
    View decorView = getWindow().getDecorView();
    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    puzzleView.setupPuzzle(new JigsawPuzzle(this, 5, 5));
  }
}
