package com.rzaaeeff.drawaletter.fragment


import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rzaaeeff.drawaletter.DrawaLetter
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
            val prefs = context!!.getSharedPreferences(DrawaLetter.SP_NAME, Context.MODE_PRIVATE)
            var taskCompleted = prefs.getBoolean(DrawaLetter.SP_COMPLETED, false)

            if (!taskCompleted) {
                taskCompleted = LetterDatabase.getInstance(context!!).letterDao().count() >= 64
                prefs.edit().putBoolean(DrawaLetter.SP_COMPLETED, taskCompleted).apply()
            }

            activity?.runOnUiThread {
                btnDraw.visibility = if (taskCompleted) View.VISIBLE else View.GONE
                tvInfo.text = getString(if (taskCompleted) R.string.task_completed else R.string.task_not_completed)
            }
        }
    }
}