import axios from 'axios';

export default class MmathService{

    betterThan( fighter1, fighter2){
        return axios.get(API.BETTER_THAN.format(fighter1, fighter2));
    }

    search(name){
        return axios.post(API.SEARCH, {name: name});
    }

    getFights(id){
        return axios.get(API.FIGHTS.format(id));
    }

    getFighter(hash){
        return axios.get(API.FIGHTER_BY_HASH.format(hash));
    }

}