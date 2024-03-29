package com.example.reader.screens.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.reader.components.FABContent
import com.example.reader.components.ListCard
import com.example.reader.components.ReaderAppBar
import com.example.reader.components.TitleSection
import com.example.reader.model.MBook
import com.example.reader.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Home(navController: NavController, vm: HomeScreenViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            ReaderAppBar(
                title = "Reader",
                navController = navController,
                showProfile = true
            )
        },
        floatingActionButton = {
            FABContent {
                navController.navigate(ReaderScreens.SearchScreen.name)
            }
        }
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                Spacer(modifier = Modifier.height(56.dp))
                HomeContent(navController, vm)
            }
        }
    }
}

@Composable
fun HomeContent(navController: NavController, vm: HomeScreenViewModel) {
    /*val listOfBooks = listOf(
        MBook("1", "Nutuk", "Ataturk", "Ey TURK Gencligi!"),
        MBook("2", "Reis", "RTE", "Ak partiiii"),
        MBook("3", "Maskulanite", "Mahmut", "Bos isler"),
        MBook("3", "Maskulanite", "Mahmut", "Bos isler"),
    )*/

    var listOfBooks = emptyList<MBook>()
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (!vm.data.value.data.isNullOrEmpty()) {
        listOfBooks = vm.data.value.data!!.toList().filter { mBook ->
            mBook.userId == currentUser?.uid.toString()
        }

        Log.d("Books", "listOfBooks: ${listOfBooks.toString()}")
    }

    val email = FirebaseAuth.getInstance().currentUser?.email

    val currentUserName = if (!email.isNullOrEmpty())
        email?.split("@")?.get(0) else "N/A"

    Column(modifier = Modifier.padding(2.dp), verticalArrangement = Arrangement.SpaceEvenly) {
        Row(modifier = Modifier.align(alignment = Alignment.Start)) {
            TitleSection(label = "You are reading \n activity right now...")

            Spacer(modifier = Modifier.fillMaxWidth(0.7f))

            Column {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    Modifier
                        .clickable {
                            navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                        }
                        .size(45.dp),
                    tint = MaterialTheme.colorScheme.secondaryContainer
                )
                Text(
                    text = currentUserName.toString(),
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Red,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
                Divider()
            }

        }

        ReadingRightNowArea(listOfBooks = listOfBooks, navController = navController)

        TitleSection(label = "Reading List")

        BookListArea(listOfBooks = listOfBooks, navController = navController)
    }
}

@Composable
fun BookListArea(listOfBooks: List<MBook>, navController: NavController) {

    val addedBooks = listOfBooks.filter { mBook ->
        mBook.startedReading == null && mBook.finishedReading == null
    }


    HorizontsScrollableComponent(addedBooks) {
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }
}

@Composable
fun HorizontsScrollableComponent(
    listOfBooks: List<MBook>,
    vm: HomeScreenViewModel = hiltViewModel(),
    onCardPressed: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(280.dp)
            .horizontalScroll(scrollState)
    ) {
        if (vm.data.value.loading == true) {
            Text(text = "Loading...")
        } else {
            if (listOfBooks.isNullOrEmpty()) {
                Surface(modifier = Modifier.padding(23.dp)) {
                    Text(text = "No books found. Add a book.")
                }
            } else {
                for (book in listOfBooks) {
                    ListCard(book) {
                        onCardPressed(book.googleBookId.toString())
                    }
                }
            }
        }


    }
}


@Composable
fun ReadingRightNowArea(listOfBooks: List<MBook>, navController: NavController) {
    val readingNowList = listOfBooks.filter { mBook ->
        mBook.startedReading != null && mBook.finishedReading == null
    }

    HorizontsScrollableComponent(listOfBooks = readingNowList) {
        navController.navigate(ReaderScreens.UpdateScreen.name+"/$it")
    }
}


