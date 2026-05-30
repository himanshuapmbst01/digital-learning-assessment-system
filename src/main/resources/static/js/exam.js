var jsonData, currentIndex = 0, qno = 1, seconds = 30, a, b, c, d, item, score = 0, total = 0;
var shuffledOptions = [], timer; // To store shuffled options and timer

// Function to shuffle options
function shuffleOptions() {
    shuffledOptions = ['A', 'B', 'C', 'D'].sort(() => Math.random() - 0.5);
}

// Function to check the selected answer
function checkAnswer() {
    if (a.checked && shuffledOptions[0] === item.correct) score++;
    else if (b.checked && shuffledOptions[1] === item.correct) score++;
    else if (c.checked && shuffledOptions[2] === item.correct) score++;
    else if (d.checked && shuffledOptions[3] === item.correct) score++;
}

// Function to display the current record with shuffled options
function displayCurrentRecord() {
    var lblqno = document.getElementById("lblqno");
    var lblquestion = document.getElementById("lblquestion");
    a = document.getElementById("a");
    b = document.getElementById("b");
    c = document.getElementById("c");
    d = document.getElementById("d");
    var lblA = document.getElementById("lblA");
    var lblB = document.getElementById("lblB");
    var lblC = document.getElementById("lblC");
    var lblD = document.getElementById("lblD");

    a.checked = b.checked = c.checked = d.checked = false; // Reset radio buttons

    if (jsonData && jsonData.length > 0) {
        item = jsonData[currentIndex];
        lblqno.innerHTML = qno;
        lblquestion.innerHTML = item.question;

        // Shuffle and display options
        shuffleOptions();
        lblA.innerHTML = item[shuffledOptions[0].toLowerCase()];
        lblB.innerHTML = item[shuffledOptions[1].toLowerCase()];
        lblC.innerHTML = item[shuffledOptions[2].toLowerCase()];
        lblD.innerHTML = item[shuffledOptions[3].toLowerCase()];
    }
}

// Function to start the timer
function startTimer() {
    var timerDisplay = document.getElementById('timer');
    seconds = 30; // Reset the timer to 30 seconds
    timerDisplay.textContent = seconds;

    clearInterval(timer); // Ensure no overlapping intervals
    timer = setInterval(function () {
        seconds--;
        timerDisplay.textContent = seconds;

        if (seconds <= 0) {
            clearInterval(timer);
            nextQuestion(); // Move to the next question when time runs out
        }
    }, 1000); // Decrease by 1 second every 1000ms (1 second)
}

// Function to move to the next question
function nextQuestion() {
    checkAnswer();
    currentIndex++;
    qno++;
    if (currentIndex === jsonData.length) {
       document.getElementById("totalInput").value = jsonData.length;
	   document.getElementById("scoreInput").value = score;
	   document.getElementById("hiddenForm").submit();
    } else {
        displayCurrentRecord();
        startTimer(); // Start the timer for the new question
    }
}

// Event listener for "Next" button
$(document).ready(function () {
    jsonData = JSON.parse($("#jsonData").val());

    // Display the first record and start the timer
    displayCurrentRecord();
    startTimer();

    $('#ButtonNext').on('click', nextQuestion);
});
