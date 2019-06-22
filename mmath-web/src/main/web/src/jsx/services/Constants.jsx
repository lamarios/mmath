var API_ROOT = "";
var API_URL = API_ROOT + '/api';

export const API = {
    MMATH: {
        BETTER_THAN: API_URL + "/better-than/{0}/{1}",
        SEARCH: API_URL + "/fighters/query",
        FIGHTS: API_URL + "/fights/{0}",
        FIGHTER_BY_HASH: API_URL + "/fighter/{0}"
    },
    EVENTS: {
        BY_ID: API_URL + "/events/{0}",
        INCOMING: API_URL + "/events/incoming",
        FIGHTS: API_URL + "/events/{0}/fights",
        EVENT_FILTERS: API_URL + "/events/organization-filters",
    },
    STATS: {
        GET_ALL_CATEGORIES: API_URL + "/stats",
        GET_CATEGORY: API_URL + "/stats/{0}",
        GET_ENTRIES: API_URL + "/stats/entries/{0}",
        GET_FOR_FIGHTER: API_URL + "/stats/for-fighter/{0}"
    }
};
