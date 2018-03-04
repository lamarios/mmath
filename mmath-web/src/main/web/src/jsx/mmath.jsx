import React from 'react';
import BetterThan from './better-than.jsx';
import FightResult from './fight-result.jsx';
import Loader from './loader.jsx';
import MmathService from './services/MmathService.jsx';

export default class Mmath extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            fighter1VsFighter2: null,
            fighter2VsFighter1: null,
            loading1v2: false,
            loading2v1: false,
            switch: false,
            fromLink: this.props.match.params.fighter1 !== undefined

        };


        this.mmathService = new MmathService();

        this.triggerSearch = this.triggerSearch.bind(this);
        this.handleSwitch = this.handleSwitch.bind(this);
    }


    /**
     * Searching for mmath results
     * @param fighter1
     * @param fighter2
     */
    triggerSearch(fighter1, fighter2) {

        this.setState({loading1v2: true, loading2v1: true, fighter1VsFighter2: null, fighter2VsFighter1: null}, () => {
            this.mmathService.betterThan(fighter1.id, fighter2.id)
                .then(res => {
                    this.setState({
                        loading1v2: false,
                        fighter1VsFighter2: res.data
                    });
                });

            this.mmathService.betterThan(fighter2.id, fighter1.id)
                .then(res => {
                    this.setState({
                        loading2v1: false,
                        fighter2VsFighter1: res.data
                    });
                });
            const pageTitle = 'Mmath - ' + fighter1.name + ' vs. ' + fighter2.name;

            if(this.state.fromLink === false) {
                window.history.pushState({}, pageTitle, '/' + fighter1.id + '/vs/' + fighter2.id);
            }
            document.title = pageTitle;
        });

    }

    /**
     * Handle when the user clicks on switch (mobile only
     */
    handleSwitch() {
        this.setState({switch: !this.state.switch});
    }

    render() {
        //Handling which column we should show first (mobile only)
        var results1v2Class = "col-md-6 column";
        if (this.state.switch === false) results1v2Class += " active";

        var results2v1Class = "col-md-6 column";
        if (this.state.switch === true) results2v1Class += " active";

        return (
            <div>
                <BetterThan fighter1={this.props.match.params.fighter1} fighter2={this.props.match.params.fighter2}
                            triggerSearch={this.triggerSearch}/>
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
                                            {this.state.fighter2VsFighter1.length > 0 ?
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
}
