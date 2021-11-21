package com.example.duksungstagram

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.duksungstagram.DetailViewFragment.Companion.contentDTOs
import com.example.duksungstagram.DetailViewFragment.Companion.contentUidList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_detail_page.*

class DetailViewPageActivity : AppCompatActivity() {
    var contentPosition: String? = null
    var contentUid: String? = null
    var contentImage: String? = null
    var contentUserId: String? = null
    var storage: FirebaseStorage? = null
    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var uid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_detail_page)

        // Initiate
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        uid = FirebaseAuth.getInstance().currentUser?.uid

        contentUid = intent.getStringExtra("myuid")
        contentImage = intent.getStringExtra("myimage")
        contentUserId = intent.getStringExtra("myid")
        contentPosition = intent.getStringExtra("position")

        comment_recyclerview.adapter = CommentRecyclerviewAdapter()
        comment_recyclerview.layoutManager = LinearLayoutManager(this)

        detailviewitem_profile_textview.text=contentUserId
        detailviewitem_explain_textview.text=contentUid
        Glide.with(this).load(contentImage).into(detailviewitem_imageview_content)

        // Add to firebase
        comment_btn_send?.setOnClickListener {
            var comment = ContentDTO.Comment()
            comment.userId = FirebaseAuth.getInstance().currentUser?.email
            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
            comment.comment = comment_edit_message.text.toString()
            comment.timestamp = System.currentTimeMillis()

            FirebaseFirestore.getInstance().collection("images").document(contentUid!!)
                .collection("comments").document().set(comment)

            comment_edit_message.setText("")
        }

        // Return to main
        back.setOnClickListener {
            finish()
        }

        // Like button is clicked
        detailviewitem_favorite_imageview.setOnClickListener {
            favoriteEvent(contentPosition!!.toInt())
        }

        // When the page is loaded
        if(contentDTOs!![contentPosition!!.toInt()].favorites.containsKey(uid)) { // like status
            detailviewitem_favorite_imageview.setImageResource(R.drawable.btn_fav_full)
        } else { // unlike status
            detailviewitem_favorite_imageview.setImageResource(R.drawable.btn_fav)
        }
    }

    inner class CommentRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var comments: ArrayList<ContentDTO.Comment> = arrayListOf()

        init {
            FirebaseFirestore.getInstance() //파이어베이스 데이터 읽어오는 코드
                .collection("images")
                .document(contentUid!!)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    comments.clear() //값이 중복으로 쌓일 수 있기 때문에 clear
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents!!) {
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                    }

                    // Refresh recyclerview
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_comment, p0, false)
            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var view = p0.itemView
            view.commentviewitem_textview_comment.text = comments[p1].comment
            view.commentviewitem_textview_profile.text = comments[p1].userId
        }

        override fun getItemCount(): Int {
            return comments.size
        }
    }

    // Like button is clicked
    fun favoriteEvent(position : Int){
        var tsDoc = firestore?.collection("images")?.document(contentUidList[position])  //내가 선택한 이미지아이디 받아오

        firestore?.runTransaction { transaction ->
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

            if(contentDTO!!.favorites.containsKey(uid)) { // containKey -> When the button is clicked -> undo like
                contentDTO?.favoriteCount = contentDTO?.favoriteCount - 1
                contentDTO?.favorites.remove(uid)

                detailviewitem_favorite_imageview.setImageResource(R.drawable.btn_fav)
            } else { // When the button is not clicked  -> add like
                contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1
                contentDTO?.favorites[uid!!] = true

                detailviewitem_favorite_imageview.setImageResource(R.drawable.btn_fav_full)
            }
            transaction.set(tsDoc,contentDTO)
        }
    }
}