import React from 'react';
import FighterSearch from './fighter-search.jsx';
import MmathService from './services/MmathService.jsx';

export default class BetterThan extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            fighter1: null, fighter2: null, preSelectedFighter1: null, preSelectedFighter2: null
        }

        this.mmathService = new MmathService();


        this.fighter1Selected = this.fighter1Selected.bind(this);
        this.fighter2Selected = this.fighter2Selected.bind(this);
    }

    componentDidMount() {
        console.log(this.props);
        if (this.props.fighter1 !== undefined
            && this.props.fighter2 !== undefined
            && this.props.fighter1.length > 0
            && this.props.fighter2.length > 0) {

            this.mmathService.getFighter(this.props.fighter1)
                .then(f1Res => {
                    this.mmathService.getFighter(this.props.fighter2)
                        .then( f2Res =>{
                            this.setState({fighter1: f1Res.data, fighter2: f2Res.data, preSelectedFighter1: f1Res.data, preSelectedFighter2: f2Res.data},
                                () =>{
                                   //now that we have all the data, we trigger the search
                                    this.props.triggerSearch(this.state.fighter1, this.state.fighter2);
                                });
                        });
                })

        }
    }

    fighter1Selected(fighter) {
        this.setState({fighter1: fighter});
    }

    fighter2Selected(fighter) {
        this.setState({fighter2: fighter});
    }


    render() {
        return (
            <div className="better-than">
                <div className="row">
                    <div className="col-md-5 fighter1">
                        <FighterSearch
                            preSelectedFighter={this.state.preSelectedFighter1}
                            fighterSelected={this.fighter1Selected}/>
                    </div>
                    <div className="col-md-2 vs">
                        VS.
                    </div>
                    <div className="col-md-5 fighter2">
                        <FighterSearch
                            preSelectedFighter={this.state.preSelectedFighter2}
                            fighterSelected={this.fighter2Selected}/>
                    </div>
                </div>
                <div className="row">
                    <div className="col-sm-12 fight-button">
                        {this.state.fighter1 !== null
                        && this.state.fighter2 !== null ?
                            <button className="btn btn-danger"
                                    onClick={this.props.triggerSearch.bind(
                                        null, this.state.fighter1,
                                        this.state.fighter2)}>
                                Fight !
                            </button>
                            :
                            ''
                        }
                    </div>
                </div>
            </div>
        );
    }
}
