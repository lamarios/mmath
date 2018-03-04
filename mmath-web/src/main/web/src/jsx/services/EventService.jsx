import axios from 'axios';

export default class EventService{

    getUpcoming(){
        return axios.get(API.EVENTS.INCOMING);
    }


    getById(id){
        return axios.get(API.EVENTS.BY_ID.format(id));
    }


    getFights(id){
        return axios.get(API.EVENTS.FIGHTS.format(id));
    }

}