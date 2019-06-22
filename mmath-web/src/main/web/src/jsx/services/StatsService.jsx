import axios from 'axios';
import {API} from "./Constants";


export default class StatsService {

    getCategories(){
        return axios.get(API.STATS.GET_ALL_CATEGORIES);
    }

    getStatsData(category){
        return axios.get(API.STATS.GET_ENTRIES.format(category));
    }
    getCategory(category){
        return axios.get(API.STATS.GET_CATEGORY.format(category));
    }


    getFighterStats(hash){
        return axios.get(API.STATS.GET_FOR_FIGHTER.format(hash));
    }

}