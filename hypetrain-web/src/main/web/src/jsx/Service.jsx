export default class Service {

    /**
     * Search for fighters by name
     * @param name the query
     * @returns {Promise<Response | never>}
     */
    searchFighter(name) {
        let formData = new FormData();
        return fetch('/api/search', {
            body: new URLSearchParams("name=" + name),
            method: 'post'
        }).then(resp => resp.json());
    }

    /**
     * Gets the top hype trains
     * @returns {Promise<Response | never>}
     */
    getTopTrains() {
        return fetch('/api/top').then(res => res.json());
    }

    /**
     * Gets a single fighter using its id
     * @param id
     * @return {Promise<Response | never>}
     */
    getFighter(id) {
        return fetch('/api/fighter/' + id).then(res => res.json());
    }


    /**
     * Gets the user login status
     * @return {Promise<Response>}
     */
    me(){
        return fetch('/api/me').then(res => {
            if(res.status === 200) {
             return   res.text()
            }else{
                throw "Not logged Int";
            }
        });
    }

    /**
     * Jump on board !
     * @param fighter
     * @return {Promise<boolean | never>}
     */
    jumpOn(fighter){
        return fetch('/api/jumpOn/'+fighter).then(res => res.status === 200);
    }

    /**
     * :(
     * @param fighter
     * @return {Promise<boolean | never>}
     */
    jumpOff(fighter){
        return fetch('/api/jumpOff/'+fighter).then(res => res.status === 200);
    }


    /**
     * Gets logged in user hype
     * @return {Promise<Response | never>}
     */
    getMyHype(){
        return fetch('/api/my-hype')
            .then(res => res.json());
    }



    postData(url = ``, data = {}) {
        // Default options are marked with *
        return fetch(url, {
            method: "POST", // *GET, POST, PUT, DELETE, etc.
            mode: "cors", // no-cors, cors, *same-origin
            cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
            credentials: "same-origin", // include, same-origin, *omit
            headers: {
                "Content-Type": "application/json; charset=utf-8",
                // "Content-Type": "application/x-www-form-urlencoded",
            },
            redirect: "follow", // manual, *follow, error
            referrer: "no-referrer", // no-referrer, *client
            body: JSON.stringify(data), // body data type must match "Content-Type" header
        })
            .then(response => response.json()); // parses response to JSON
    }
}