package io.github.pshegger.gamedevexperiments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import io.github.pshegger.gamedevexperiments.scenes.menu.MainMenuScene

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var instance: MainActivity
    }

    lateinit var gameSurface: GameSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        instance = this

        gameSurface = findViewById(R.id.gameSurface)
        gameSurface.scene = MainMenuScene(gameSurface)
    }

    override fun onBackPressed() {
        gameSurface.scene?.onBackPressed() ?: super.onBackPressed()
    }
}
