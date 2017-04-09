var BetterThan = require('./better-than');
var FightResult = require('./fight-result');
var Loader = require('./loader');

var Mmath = React.createClass({
    getInitialState: function () {
        return {
            fighter1VsFighter2: null,
            fighter2VsFighter1: null,
            loading1v2: false,
            loading2v1: false,
            switch: false
        };
    },

    /**
     * Searching for mmath results
     * @param fighter1
     * @param fighter2
     */
    triggerSearch: function (fighter1, fighter2) {
        console.log(fighter1);
        console.log(fighter2);

        this.setState({loading1v2: true, loading2v1: true, fighter1VsFighter2: null, fighter2VsFighter1: null});

        // searching fightre 1 vs fighter 2
        $.ajax({
            method: 'GET',
            url: '/api/better-than/' + fighter1.id + '/'
            + fighter2.id,
            dataType: 'json',
            cache: false,
            success: function (data) {
                console.log(data);
                this.setState({
                    loading1v2: false,
                    fighter1VsFighter2: data
                });
            }.bind(this)
        });

        //serching fighter 2 vs fighter 1
        $.ajax({
            method: 'GET',
            url: '/api/better-than/' + fighter2.id + '/'
            + fighter1.id,
            dataType: 'json',
            cache: false,
            success: function (data) {
                console.log(data);
                this.setState({
                    loading2v1: false,
                    fighter2VsFighter1: data
                });
            }.bind(this)
        });
    },

    /**
     * Handle when the user clicks on switch (mobile only
     */
    handleSwitch: function () {
        this.setState({switch: !this.state.switch});
    },

    /**
     * Rendering the content
     * @returns {XML}
     */
    render: function () {

        //Handling which column we should show first (mobile only)
        var results1v2Class = "col-md-6 column";
        if (this.state.switch === false) results1v2Class += " active";

        var results2v1Class = "col-md-6 column";
        if (this.state.switch === true) results2v1Class += " active";

        return (
            <div>
                <BetterThan triggerSearch={this.triggerSearch}/>
                <div className="results row">
                    <div className="col-sm-12">

                        {this.state.loading1v2 || this.state.loading2v1 && <Loader/>}

                        {
                            this.state.fighter1VsFighter2 !== null && this.state.fighter2VsFighter1 !== null ?
                                <div>
                                    <div className="switch">
                                        <button className="btn btn-primary" onClick={this.handleSwitch}>
                                            Switch
                                        </button>
                                    </div>
                                    <div className="row">
                                        <div className={results1v2Class}>
                                            {this.state.fighter1VsFighter2.length > 0 ?
                                                <FightResult results={this.state.fighter1VsFighter2}/>
                                                : <p className="nope">Nope...</p>}
                                        </div>
                                        <div className={results2v1Class}>
                                            { this.state.fighter2VsFighter1.length > 0 ?
                                                <FightResult results={this.state.fighter2VsFighter1}/>
                                                : <p className="nope">Nope...</p>}
                                        </div>
                                    </div>
                                </div>
                                : ''
                        }
                    </div>
                </div>
            </div>
        )
    }

});

ReactDOM.render(
    <Mmath/>
    ,
    document.getElementById('content')
);