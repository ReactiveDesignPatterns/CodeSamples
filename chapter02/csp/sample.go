package main                                                                                                                                                           

import (
    "fmt"
    "time"
)

func main() {
	iterations := 10
    myChannel := make(chan int)

    go producer(myChannel, iterations)
    go consumer(myChannel, iterations)

    time.Sleep(500 * time.Millisecond)
}

func producer(myChannel chan int, iterations int) {
    for i := 1; i <= iterations; i++ {
        fmt.Println("Sending: ", i)
        myChannel <- i
    }   
}

func consumer(myChannel chan int, iterations int) {
    for i := 1; i <= iterations; i++ {
        recVal := <-myChannel
        fmt.Println("Received: ", recVal)
    }   
}
