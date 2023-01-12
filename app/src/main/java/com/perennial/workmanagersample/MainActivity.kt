package com.perennial.workmanagersample

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.perennial.workmanagersample.viewModel.DownloadImageViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private val viewModel: DownloadImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progress_circular.visibility = View.VISIBLE
        viewModel.performImageDownloadWork()
        observerDownloadImageProgress()
    }

    private fun observerDownloadImageProgress(){
        viewModel.isImageDownloadingFinished.observe(this) { it ->
            if (it != null && it.state.isFinished){
                getBitmapCacheAndLoad()
            }
        }
    }

    private fun getBitmapCacheAndLoad(){
        val filename = "map.jpg"
        val cacheFile = File(applicationContext.cacheDir, filename)
        val picture = BitmapFactory.decodeFile(cacheFile.path)
        progress_circular.visibility = View.GONE
        ivDownloadedImage.visibility = View.VISIBLE
        ivDownloadedImage.setImageBitmap(picture)
    }
}