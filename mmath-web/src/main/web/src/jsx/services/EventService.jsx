import axios from 'axios';
import {API} from "./Constants";

export default class EventService {

    /**
     *
     * @param page
     * @param orgs array of organization ids
     * @returns {AxiosPromise}
     */
    getUpcoming(page, orgs) {
        var query = '';
        if (page === undefined) {
            query += '?page=1';
        } else {
            query += '?page=' + page
        }

        if (orgs !== undefined && orgs !== null && orgs.length > 0) {
            query += '&organizations=' + orgs.join(',');
        }

        return axios.get(API.EVENTS.INCOMING + query);
    }


    getById(id) {
        return axios.get(API.EVENTS.BY_ID.format(id));
    }


    getFights(id) {
        return axios.get(API.EVENTS.FIGHTS.format(id));
    }

    getOrganizationsForFilter() {
        return axios.get(API.EVENTS.EVENT_FILTERS);
    }

}