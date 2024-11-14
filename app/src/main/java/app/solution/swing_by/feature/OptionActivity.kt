package app.solution.swing_by.feature

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import app.solution.swing_by.api.FirebaseAPI
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
                showSelectDialog("겸색 갯수", arrayOf("1개", "3개", "5개"))
            }

            layoutSearchingDistance.setOnClickListener {
                showSelectDialog("겸색 거리", arrayOf("500m", "1000m", "2000m"))
            }

            btnAccountDelete.setOnClickListener {
                showConfirmDialog()
            }

            btnLogout.setOnClickListener {
                logout()
            }
        }
    }

    private fun showSelectDialog(title: String, items: Array<String>) {
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

    private fun showConfirmDialog() {
        AlertDialog.Builder(this@OptionActivity).apply {
            setTitle("회원탈퇴")
                .setMessage("탈퇴 시 데이터는 복구 되지 않습니다.")
                .setPositiveButton("확인") { dialog, id ->
                    FirebaseAPI.deleteAccount(object : FirebaseAPI.Callback {
                        override fun successCallback() {
                            finishAffinity()

                            Intent(this@OptionActivity, AuthActivity::class.java).apply {
                                startActivity(this)
                            }
                        }

                        override fun failureCallback() {}
                    })
                }
                .setNegativeButton("취소") { dialog, id ->
                    dialog.dismiss()
                }
            show()
        }
    }

    private fun logout() {
        CoroutineScope(Dispatchers.Main).launch {
            MyApplication.getInstance().getLocalDataManager().setString(LocalDataConstant.UID, "")
        }

        finishAffinity()

        Intent(this@OptionActivity, AuthActivity::class.java).apply {
            startActivity(this)
        }
    }
}