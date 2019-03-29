function getLocalStorage() {
    try {
        if(window.localStorage ) return window.localStorage;
    }
    catch (e)
    {
        return undefined;
    }
}


 var db = getLocalStorage();