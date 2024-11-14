package app.solution.swing_by.feature

import androidx.appcompat.app.AlertDialog
import app.solution.swing_by.base.BaseActivity
import app.solution.swing_by.constant.LocalDataConstant
import app.solution.swing_by.databinding.ActivityOptionBinding
import app.solution.swing_by.root.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OptionActivity : BaseActivity<ActivityOptionBinding>(ActivityOptionBinding::inflate) {
    override fun initView() {
        super.initView()

        setDatas()
    }

    override fun initListener() {
        super.initListener()

        setButtons()
    }

    private fun setDatas() {
        CoroutineScope(Dispatchers.Main).launch {
            with(binding) {
                tvSearchingDistance.text = "${MyApplication.getInstance().getLocalDataManager().getString(LocalDataConstant.OPTION_SEARCHING_DISTANCE, "500")}m"
                tvSearchingLimit.text = "${MyApplication.getInstance().getLocalDataManager().getString(LocalDataConstant.OPTION_SEARCHING_LIMIT, "3")}개"
            }
        }
    }

    private fun setButtons() {
        with(binding) {
            layoutSearchingLimit.setOnClickListener {
                dialog("겸색 갯수", arrayOf("1개", "3개", "5개"))
            }

            layoutSearchingDistance.setOnClickListener {
                dialog("겸색 거리", arrayOf("500m", "1000m", "2000m"))
            }

            btnAccountDelete.setOnClickListener {

            }

            btnLogout.setOnClickListener {

            }
        }
    }

    private fun dialog(title: String, items: Array<String>) {
        AlertDialog.Builder(this).run {
            setTitle(title)
            setItems(items) { p0, p1 ->
                CoroutineScope(Dispatchers.Main).launch {
                    val data = items[p1].replace("\\D".toRegex(), "")

                    if (items[p1].contains("개")) {
                        MyApplication.getInstance().getLocalDataManager().setString(LocalDataConstant.OPTION_SEARCHING_LIMIT, data)
                    } else if (items[p1].contains("m")) {
                        MyApplication.getInstance().getLocalDataManager().setString(LocalDataConstant.OPTION_SEARCHING_DISTANCE, data)
                    }
                }.invokeOnCompletion {
                    setDatas()
                }
            }
            setNegativeButton("취소", null)
            show()
        }
    }
}