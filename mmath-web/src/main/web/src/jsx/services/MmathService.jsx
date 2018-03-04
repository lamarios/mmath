import axios from 'axios';

export default class MmathService{

    betterThan( fighter1, fighter2){
        return axios.get(API.MMATH.BETTER_THAN.format(fighter1, fighter2));
    }

    search(name){
        return axios.post(API.MMATH.SEARCH, {name: name});
    }

    getFights(id){
        return axios.get(API.MMATH.FIGHTS.format(id));
    }

    getFighter(hash){
        return axios.get(API.MMATH.FIGHTER_BY_HASH.format(hash));
    }

}