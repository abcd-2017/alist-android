package com.android.alist.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.alist.App
import com.android.alist.ui.compose.PageConstant
import com.android.alist.ui.compose.file.FilePage
import com.android.alist.ui.compose.service.ServicePage
import com.android.alist.ui.theme.AlistandroidTheme
import com.android.alist.utils.SharePreferenceUtils
import com.android.alist.utils.constant.AppConstant
import com.android.alist.utils.constant.HttpStatusCode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val coroutineScope = rememberCoroutineScope()

            App.globalRequestBeforeCallback = {
                if (SharePreferenceUtils.getData(AppConstant.APP_NAME, "").isBlank()) {
                    coroutineScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            App.context,
                            "请登录",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.popBackStack()
                        navController.navigate("${PageConstant.Service.text}/true")
                    }
                }
            }
            App.globalRequestAfterCallback = { response ->
                if (response.code == HttpStatusCode.Unauthorized.code) {
                    SharePreferenceUtils.saveData(AppConstant.TOKEN, "")
                    coroutineScope.launch(Dispatchers.Main) {
                        delay(200)
                        Toast.makeText(
                            App.context,
                            "请重新登录",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.popBackStack()
                        navController.navigate("${PageConstant.Service.text}/true")
                    }
                }
            }

            AlistandroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorScheme.background
                ) {
                    NavHost(
                        navController,
                        startDestination = "${PageConstant.Service.text}/false"
                    ) {
                        composable(
                            route = "${PageConstant.Service.text}/{changeService}",
                            arguments = listOf(
                                navArgument("changeService") {
                                    type = NavType.BoolType
                                    defaultValue = false
                                }
                            )
                        ) { entity ->
                            ServicePage(
                                onBackPressedDispatcher,
                                navController,
                                changeService = entity.arguments?.getBoolean("changeService") == true
                            )
                        }
                        composable(PageConstant.File.text) {
                            FilePage(navController, onBackPressedDispatcher)
                        }
                    }
                }
            }
        }
    }
}