 
listView('asd Jobs') {
    description('asd Jobs')
    jobs {
        regex('asd_.+')
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}
