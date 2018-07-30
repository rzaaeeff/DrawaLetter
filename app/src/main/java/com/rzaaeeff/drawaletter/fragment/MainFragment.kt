package com.rzaaeeff.drawaletter.fragment


import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rzaaeeff.drawaletter.R
import com.rzaaeeff.drawaletter.persistence.LetterDatabase
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        super.onResume()

        AsyncTask.execute {
            val isEligibleForFreeMode  = LetterDatabase.getInstance(context!!).letterDao().count() >= 64

            activity?.runOnUiThread {
                btnDraw.visibility = if (isEligibleForFreeMode) View.VISIBLE else View.GONE
            }
        }
    }
}
