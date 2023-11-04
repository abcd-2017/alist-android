package com.android.alist.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.alist.ui.compose.PageConstant
import com.android.alist.ui.compose.service.ServicePage
import com.android.alist.ui.theme.AlistandroidTheme
import com.android.alist.utils.HttpStatusCode
import com.android.alist.viewmodel.ServiceViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlistandroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val serviceViewModel: ServiceViewModel = viewModel()
                    val navController = rememberNavController()

                    //页面导航
                    LoginNavHost(navController)
                }
            }
        }
    }
}

@Composable
fun LoginNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = PageConstant.LoginPage.ManageServer.name
    ) {
        composable(
            route = PageConstant.LoginPage.ManageServer.name
        ) {
            ServicePage()
        }


    }
}