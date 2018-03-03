import React from 'react';
import FighterTypeAhead from './fighter-type-ahead.jsx';
import FighterChip from './fighter-chip.jsx';
import Loader from './loader.jsx';
import MmathService from './services/MmathService.jsx';


export default class FighterSearch extends React.Component {

    constructor(props) {
        super(props);

        this.mmathService = new MmathService();

        this.state = {
            fighters: [],
            query: '',
            selected: null,
            loading: false,
            preSelectionHappened: false
        };


        this.loadFighters = this.loadFighters.bind(this);
        this.handleFighterSelection = this.handleFighterSelection.bind(this);
        this.handleSearch = this.handleSearch.bind(this);
        this.removeSelected = this.removeSelected.bind(this);

    }

    componentWillReceiveProps(props){
        console.log('received prop', props);
        if(props.preSelectedFighter !== null && !this.state.preSelectionHappened){
            console.log('doing something');
            const fighter = Object.assign({}, props.preSelectedFighter);
            this.setState({selected: fighter, preSelectionHappened: true});
        }
    }

    //loads fighters based on the text search
    loadFighters(name) {

        this.setState({loading: true}, () => {
            this.mmathService.search(name).then(res => {
                this.setState({loading: false, fighters: res.data});
            })
        });
    }


    //Triggers the search
    handleSearch(e) {
        console.log(e);
        var query = e.target.value;
        if (query.length >= 3) {
            clearTimeout(this.state.timeout);
            this.state.timeout =
                setTimeout(function () {
                    this.loadFighters(query)
                }.bind(this), 300);
        } else {
            this.setState({fighters: []});
        }
    }

    //Handles the click when the user clicks on the fighter
    // he wants to choose
    handleFighterSelection(fighter) {
        this.setState({selected: fighter});
        this.props.fighterSelected(fighter);
    }

    //Remove the selected fighter
    removeSelected(e) {
        console.log('removing !', e);
        this.setState({selected: null, fighters: []});
        this.props.fighterSelected(null);
        e.stopPropagation();

    }

    render() {
        if (this.state.selected === null) {
            return (
                <div className="fighter-search">
                    {this.state.loading &&
                    <div className="search-loader">
                        <Loader/>
                    </div>
                    }
                    <input className="form-control"
                           type="text"
                           autoComplete="off" autoCorrect="off" autoCapitalize="off" spellCheck="false"
                           onKeyUp={this.handleSearch}
                           placeholder="Search for fighter"
                    />
                    {this.state.fighters.length > 0 &&
                    <FighterTypeAhead onClick={this.handleFighterSelection} fighters={this.state.fighters}/>
                    }
                </div>
            );
        } else {
            return (
                <FighterChip fighter={this.state.selected}
                             removable={true}
                             onClick={this.removeSelected}/>
            );
        }
    }
}

