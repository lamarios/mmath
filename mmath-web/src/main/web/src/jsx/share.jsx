import React from 'react';


export default class Share extends React.Component {

    constructor() {
        super();
        this.share = this.share.bind(this);
    }

    share(){
        prompt("Spread the truth", window.location);
    }

    render() {
        return (
            <button type="button" class="btn btn-secondary btn-sm" onClick={this.share}>Share</button>
        )
    }
}