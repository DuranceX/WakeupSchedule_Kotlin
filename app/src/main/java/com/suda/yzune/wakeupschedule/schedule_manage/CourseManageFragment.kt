package com.suda.yzune.wakeupschedule.schedule_manage


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import es.dmoral.toasty.Toasty

class CourseManageFragment : Fragment() {

    private lateinit var viewModel: ScheduleManageViewModel
    private var tablePosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleManageViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_course_manage, container, false)
        val rvCourseList = view.findViewById<RecyclerView>(R.id.rv_course_list)
        tablePosition = arguments!!.getInt("position")
        viewModel.getCourseBaseBeanListByTable(viewModel.tableSelectList[tablePosition].id).observe(this, Observer {
            if (it == null) return@Observer
            viewModel.courseList.clear()
            viewModel.courseList.addAll(it)
            if (rvCourseList.adapter == null) {
                initRecyclerView(rvCourseList, viewModel.courseList)
            } else {
                rvCourseList.adapter.notifyDataSetChanged()
            }
        })
        return view
    }

    private fun initRecyclerView(rvCourseList: RecyclerView, data: List<CourseBaseBean>) {
        rvCourseList.layoutManager = LinearLayoutManager(context)
        val adapter = CourseListAdapter(R.layout.item_course_list, data)
        adapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.ib_edit -> {
                    val intent = Intent(activity, AddCourseActivity::class.java)
                    intent.putExtra("id", data[position].id)
                    intent.putExtra("tableId", data[position].tableId)
                    intent.putExtra("maxWeek", viewModel.tableSelectList[tablePosition].maxWeek)
                    startActivity(intent)
                }
                R.id.ib_delete -> {
                    Toasty.info(activity!!.applicationContext, "长按删除课程哦~").show()
                }
            }
        }
        adapter.setOnItemChildLongClickListener { _, view, position ->
            when (view.id) {
                R.id.ib_delete -> {
                    viewModel.deleteCourse(data[position])
                    return@setOnItemChildLongClickListener true
                }
                else -> {
                    return@setOnItemChildLongClickListener false
                }
            }

        }
        adapter.addFooterView(initFooterView())
        rvCourseList.adapter = adapter
    }

    private fun initFooterView(): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_add_course_btn, null)
        val tvBtn = view.findViewById<TextView>(R.id.tv_add)
        tvBtn.text = "添加"
        tvBtn.setOnClickListener {
            val intent = Intent(activity, AddCourseActivity::class.java)
            intent.putExtra("tableId", viewModel.tableSelectList[tablePosition].id)
            intent.putExtra("maxWeek", viewModel.tableSelectList[tablePosition].maxWeek)
            intent.putExtra("id", -1)
            startActivity(intent)
        }
        return view
    }

}
