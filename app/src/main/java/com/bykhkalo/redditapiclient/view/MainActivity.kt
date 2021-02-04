package com.bykhkalo.redditapiclient.view

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bykhkalo.redditapiclient.R
import com.bykhkalo.redditapiclient.utils.DebugUtils.Companion.TAG
import com.bykhkalo.redditapiclient.viewmodel.MainViewModel
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig
import com.memebattle.pwc.util.NetworkState
import com.stfalcon.frescoimageviewer.ImageViewer
import java.util.*


/*

 1. Автор
 2. Дата
 3. Ескіз картинки
 4. Картинка
 5. Кількість коментарів
 6. Id
 7. after

 */


class MainActivity : AppCompatActivity(), RedditPostAdapter.ActivityCallbackOnClick {

    private lateinit var viewModel: MainViewModel

    private lateinit var postList: RecyclerView
    private lateinit var adapter: RedditPostAdapter

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var currentImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.let {
            currentImageUrl = savedInstanceState.getString("imageUrl")
            Log.d(TAG, "onCreate: Restored")
        }

        init()
    }

    private fun init() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        swipeRefreshLayout = findViewById(R.id.swipe_refresh)

        if (currentImageUrl != null) {
            showImageCallback(currentImageUrl)
        }

        initImageRecyclerView()
        initSwipeToRefresh()

        viewModel.beginLoading()
    }


    private fun initImageRecyclerView() {
        postList = findViewById(R.id.post_list)

        val adapter = RedditPostAdapter({
            viewModel.retry()
        }, this)


        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) postList.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        else postList.layoutManager = LinearLayoutManager(this)

        postList.adapter = adapter

        viewModel.posts.observe(this, {
            adapter.submitList(it)
        })

        viewModel.networkState.observe(this, {
            adapter.setNetworkState(it)
        })



    }

    private fun initSwipeToRefresh() {
        viewModel.refreshState.observe(this, {
            swipeRefreshLayout.isRefreshing = it == NetworkState.LOADING
        })
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    override fun showImageCallback(imageUrl: String?) {
        //This method for recyclerView item.onClick call


        if (imageUrl != null && !imageUrl.isEmpty()) {
            Log.d(TAG, "showImageCallback: url: $imageUrl")
            currentImageUrl = imageUrl

            if (currentImageUrl!!.contains(".jpg") || currentImageUrl!!.contains(".png")){


                val config = ImagePipelineConfig.newBuilder(this)
                    .setProgressiveJpegConfig(SimpleProgressiveJpegConfig())
                    .setResizeAndRotateEnabledForNetwork(true)
                    .setDownsampleEnabled(true)
                    .build()

                Fresco.initialize(this, config)

                val imageOverlayView = ImageOverlayView(this, imageUrl)

                ImageViewer.Builder(this, Arrays.asList(imageUrl))
                    .hideStatusBar(true)
                    .allowSwipeToDismiss(true)
                    .setOnDismissListener { currentImageUrl = null }
                    .setOverlayView(imageOverlayView)
                    .show()

            }
            else {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(currentImageUrl)
                    )
                )
            }

        } else {
            Toast.makeText(this, "Error with url", Toast.LENGTH_SHORT).show();
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("imageUrl", currentImageUrl)
    }

}