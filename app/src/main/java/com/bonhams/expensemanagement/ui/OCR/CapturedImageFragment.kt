package com.bonhams.expensemanagement.ui.OCR

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.newClaim.OcrNewClaimFragment
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.rmExpence.ZoomOutPageTransformer
import com.bonhams.expensemanagement.utils.RecylerCallback
import com.bonhams.expensemanagement.utils.RefreshPageListener
import com.bonhams.expensemanagement.utils.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*


class CapturedImageFragment : Fragment(), RecylerCallback, RefreshPageListener {

    private var contextActivity: BaseActivity? = null
    private var adapter: ScanImagePager2Adapter? = null
    private lateinit var progDialog: ProgressDialog
    //private lateinit var imageView: ImageView
    private lateinit var viewPager2: ViewPager2
    private lateinit var txtFinish: TextView
    private lateinit var txtRetak: TextView
    private lateinit var dotsIndicator: WormDotsIndicator
    private lateinit var viewModel: OcrViewModel

    var imgPostion=0
        var reTake=false
    var bitmapArray = ArrayList<Bitmap>()
    var reciptList: MutableList<String?> = mutableListOf<String?>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_capture, container, false)
        contextActivity = activity as? BaseActivity
        setupViewModel()
        (contextActivity as MainActivity).setAppbarTitle(getString(R.string.scan_receipt))
        (contextActivity as MainActivity).showBottomNavbar(false)
        (contextActivity as MainActivity).showAppbarBackButton(true)

        val startStop = view.findViewById(R.id.ivAddImage) as ImageView?
         txtFinish = (view.findViewById(R.id.txtFinish) as TextView?)!!
        txtRetak = (view.findViewById(R.id.reTake) as TextView?)!!
       //  imageView = (view.findViewById(R.id.imageView2) as ImageView?)!!
        viewPager2 = (view.findViewById(R.id.viewPager2) as ViewPager2?)!!
        dotsIndicator = (view.findViewById(R.id.dotsIndicator) as WormDotsIndicator?)!!
        progDialog= ProgressDialog(requireContext())
      //  progDialog.setTitle("Getting current location...")
       // progDialog.show()

        startStop?.setOnClickListener {
            reTake=false

            showBottomSheet()

        }
        txtRetak.setOnClickListener {
            println("bitmapArray size:" + bitmapArray.size)
            reTake=true
            showBottomSheet()

        }



        txtFinish.setOnClickListener {
            try {
                println("bitmapArray size:" + bitmapArray.size)

                if(bitmapArray.size>1) {
                    println("single bitmap:" + combineImageIntoOne(bitmapArray))
                    reciptList[0]?.let { it1 -> setObserver(combineImageIntoOne(bitmapArray), it1) }
                }else{
                    reciptList[0]?.let { it1 -> setObserver(bitmapArray[0], it1) }
                }
            } catch (e: Exception) {
            }
        }

         adapter = ScanImagePager2Adapter(requireContext(), this,bitmapArray)
        viewPager2.adapter = adapter
        val zoomOutPageTransformer = ZoomOutPageTransformer()
        viewPager2.setPageTransformer { page, position ->
            zoomOutPageTransformer.transformPage(page, position)
        }

        dotsIndicator.setViewPager2(viewPager2)
        return view
    }
    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity(),
            OcrViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(OcrViewModel::class.java)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun addFragment(fragment: Fragment) {
        contextActivity?.supportFragmentManager?.beginTransaction()?.add(
            R.id.container,
            fragment,
            fragment.javaClass.simpleName
        )?.addToBackStack(fragment.javaClass.simpleName)?.commit()
    }
    fun setViewPager( attachmentList: ArrayList<Bitmap>) {
        val adapter = ScanImagePager2Adapter(requireContext(), this,attachmentList)
        viewPager2.adapter = adapter
        val zoomOutPageTransformer = ZoomOutPageTransformer()
        viewPager2.setPageTransformer { page, position ->
            zoomOutPageTransformer.transformPage(page, position)
        }

        dotsIndicator.setViewPager2(viewPager2)
    }
    fun setViewPagerRefresh( ) {
        adapter?.notifyDataSetChanged()
    }

    private fun setObserver(imageBitmap:Bitmap?,fileTpe:String) {
        progDialog.show()
/*
        val stream = ByteArrayOutputStream()
        if(fileTpe.endsWith(".png")){
            imageBitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }else{
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        }*/
        val file = File(
            fileTpe
        )
       // val image = stream.toByteArray()
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("user_id", "a40668de-3750-4d5b-974b-bae2450e7a0b")
                    builder.addFormDataPart("image_name", "test", RequestBody.create(
                        MultipartBody.FORM, file))



        val mrequestBody: RequestBody = builder.build()


        viewModel.uploadOCRImage(mrequestBody).observe(viewLifecycleOwner) {

            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                progDialog.dismiss()
                                response.mapping_data?.attachmentsList=reciptList
                                        val fragment = OcrNewClaimFragment()
                                        fragment.setOcrDetails(response.mapping_data)
                                        fragment.setRefreshPageListener(this)
                                        (contextActivity as? MainActivity)?.addFragment(fragment)


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
//                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }





        }
    }
    private fun showBottomSheet(){
        contextActivity?.let {
            val dialog = BottomSheetDialog(contextActivity!!, R.style.CustomBottomSheetDialogTheme)
            val view = layoutInflater.inflate(R.layout.item_bottom_sheet, null)
            dialog.setCancelable(true)
            dialog.setContentView(view)
            val bottomOptionOne = view.findViewById<TextView>(R.id.bottomOptionOne)
//            val dividerOne = view.findViewById<View>(R.id.dividerOne)
            val bottomOptionTwo = view.findViewById<TextView>(R.id.bottomOptionTwo)
            val dividerTwo = view.findViewById<View>(R.id.dividerTwo)
            val bottomOptionThree = view.findViewById<TextView>(R.id.bottomOptionThree)
            val bottomOptionCancel = view.findViewById<TextView>(R.id.bottomOptionCancel)

            bottomOptionOne.text = resources.getString(R.string.upload_file)
            bottomOptionTwo.text = resources.getString(R.string.take_photo)
            dividerTwo.visibility = View.GONE
            bottomOptionThree.visibility = View.GONE

            bottomOptionOne.setOnClickListener {
                dialog.dismiss()
                choosePhotoFromGallery()
            }
            bottomOptionTwo.setOnClickListener {
                dialog.dismiss()
                takePhotoFromCamera()
            }
            bottomOptionCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
    private fun choosePhotoFromGallery() {
        contextActivity?. let {
            val intent = Lassi(contextActivity!!)
                .with(LassiOption.CAMERA_AND_GALLERY) // choose Option CAMERA, GALLERY or CAMERA_AND_GALLERY
                .setMaxCount(5)
                .setGridSize(3)
                .setMediaType(MediaType.IMAGE) // MediaType : VIDEO IMAGE, AUDIO OR DOC
                //.setCompressionRation(50) // compress image for single item selection (can be 0 to 100)
                // .setMinFileSize(50) // Restrict by minimum file size
                // .setMaxFileSize(100) //  Restrict by maximum file size
                //.disableCrop() // to remove crop from the single image selection (crop is enabled by default for single image)
                .setStatusBarColor(R.color.secondary)
                .setToolbarResourceColor(R.color.white)
                .setProgressBarColor(R.color.secondary)
                .setToolbarColor(R.color.secondary)
                .setPlaceHolder(R.drawable.ic_image_placeholder)
                .setErrorDrawable(R.drawable.ic_image_placeholder)
                .build()
            startActivityForResult(intent, 100)
        }
    }

    private fun takePhotoFromCamera(){
        contextActivity?. let {
            val intent = Lassi(contextActivity!!)
                .with(LassiOption.CAMERA) // choose Option CAMERA, GALLERY or CAMERA_AND_GALLERY
                .setMaxCount(5)
                .setGridSize(3)
                .enableRotate()
                .setMediaType(MediaType.IMAGE) // MediaType : VIDEO IMAGE, AUDIO OR DOC
               // .setCompressionRation(50) // compress image for single item selection (can be 0 to 100)
                //.setMinFileSize(50) // Restrict by minimum file size
               // .setMaxFileSize(100) //  Restrict by maximum file size
               // .disableCrop() // to remove crop from the single image selection (crop is enabled by default for single image)
                .setStatusBarColor(R.color.secondary)
                .setToolbarResourceColor(R.color.white)
                .setProgressBarColor(R.color.secondary)
                .setToolbarColor(R.color.secondary)
                .setPlaceHolder(R.drawable.ic_image_placeholder)
                .setErrorDrawable(R.drawable.ic_image_placeholder)
                .build()
            startActivityForResult(intent, 101)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            txtRetak.visibility=View.VISIBLE
            when (requestCode) {
                100 -> {
                    //bitmapArray.clear()
                    val selectedMedia = data.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                    Log.d("capatured Image", "onActivityResult: ${selectedMedia.size}")
                    if(selectedMedia.size > 0) {

                        selectedMedia.forEach {
                            val file= File( it.path!!)
                            val filePath: String = file.path
                            val bitmap = BitmapFactory.decodeFile(filePath)
                            Log.d("capatured Image", "path: ${file.path}")

                            if(reTake){
                                reTake=false
                                bitmapArray.removeAt(imgPostion)
                                bitmapArray.add(imgPostion,bitmap)
                                reciptList.removeAt(imgPostion)
                                reciptList.add(imgPostion,it.path!!)
                            }else{
                                bitmapArray.add(bitmap)
                                reciptList.add(it.path!!)


                            }
                        }


                    }
                   // setViewPager(bitmapArray)
                    setViewPagerRefresh()
                    //imageView.setImageBitmap(combineImageIntoOne(bitmapArray))
                    Log.d("bitmapArray Image", "onActivityResult: ${bitmapArray.size}")

                   // imageView.setImageBitmap(mergeTwoBitmapWithOverlapping(bitmapArray[0],bitmapArray[1]))

                }
                101 -> {
                   // bitmapArray.clear()
                    val selectedMedia = data.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                    Log.d("capatured Image camera", "onActivityResult: ${selectedMedia.size}")
                    if(selectedMedia.size > 0) {
                        val file= File( selectedMedia[0].path!!)
                        val filePath: String = file.path
                        val bitmap = BitmapFactory.decodeFile(filePath)

                        if(reTake){
                            reTake=false
                            bitmapArray.removeAt(imgPostion)
                            bitmapArray.add(imgPostion,bitmap)
                            reciptList.removeAt(imgPostion)
                            reciptList.add(imgPostion,selectedMedia[0].path!!)
                        }else{
                            bitmapArray.add(bitmap)
                            reciptList.add(selectedMedia[0].path!!)


                        }
                    }
                    setViewPagerRefresh()

                    // imageView.setImageBitmap(combineImageIntoOne(bitmapArray))
                }
            }
        }
    }

    @Throws(IOException::class)
    fun getBytes(`is`: InputStream): ByteArray? {
        val byteBuff = ByteArrayOutputStream()
        val buffSize = 1024
        val buff = ByteArray(buffSize)
        var len = 0
        while (`is`.read(buff).also { len = it } != -1) {
            byteBuff.write(buff, 0, len)
        }
        return byteBuff.toByteArray()
    }

    // Cobine Multi Image Into One
    private fun combineImageIntoOne(bitmap: ArrayList<Bitmap>): Bitmap? {
        var w = 0
        var h = 0
        for (i in bitmap.indices) {
         if (i < bitmap.size - 1) {
                w =
                    if (bitmap[i].width > bitmap[i + 1].width) bitmap[i].width else bitmap[i + 1].width
            }
           // w =bitmap[i].width
            h += bitmap[i].height
        }
        val temp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(temp)
        var top = 0
        for (i in bitmap.indices) {
            Log.d("HTML", "Combine: " + i + "/" + bitmap.size + 1)
            top = if (i == 0) 0 else top + bitmap[i].height
            canvas.drawBitmap(bitmap[i], 0f, top.toFloat(), null)
        }
        return temp
    }

    fun mergeTwoBitmapWithOverlapping(up: Bitmap, down: Bitmap): Bitmap? {
        var cs: Bitmap? = null
        val width: Int
        var height = 0
        val first = Bitmap.createScaledBitmap(
            up,
            up.width / 2, up.height / 2, true
        )
        val second = Bitmap.createScaledBitmap(
            down,
            (down.width / 1.5).toInt(),
            (down.height / 1.5).toInt(), true
        )
        width = up.width
        height = up.height
        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val twiseImage = Canvas(cs)
        twiseImage.drawBitmap(
            first, (width - (first.width * 2)).toFloat(), (
                    height - first.height).toFloat(), null
        )
        twiseImage.drawBitmap(second, (width - second.width).toFloat(), 0f, null)
        return cs
    }

    override fun callback(action: String, data: Any, postion: Int) {
       // println("postion:" + postion)
        imgPostion =postion
        // TODO("Not yet implemented")
    }

    override fun refreshPage() {

    }

}