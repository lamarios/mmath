import axios from 'axios';


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

}