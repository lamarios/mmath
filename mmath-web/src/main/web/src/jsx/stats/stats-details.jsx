import React from 'react';
import StatsService from '../services/StatsService.jsx';
import StatsEntry from './stats-entry.jsx';
import FighterDetails from '../fighter-details.jsx';

export default class StatsDetails extends React.Component {

    constructor(props) {
        super(props);
        console.log('props', this.props);
        this.state = {entries: [], category: null, selectedFighter: null};

        this.statsService = new StatsService();
        this.refreshData = this.refreshData.bind(this);
        this.showFighter = this.showFighter.bind(this);
        this.closeDetails = this.closeDetails.bind(this);


    }

    componentDidMount() {
        this.refreshData(this.props.match.params.cat);
    }

    componentWillReceiveProps(props) {
        this.refreshData(props.match.params.cat);
    }

    showFighter(fighter) {
        this.setState({selectedFighter: fighter});
    }

    closeDetails(){
        this.setState({selectedFighter:null})
    }

    refreshData(cat) {
        this.statsService.getStatsData(cat)
            .then(res => {
                this.setState({entries: res.data});
            });

        this.statsService.getCategory(cat)
            .then(res => this.setState({category: res.data}));
    }

    render() {
        return (
            <div>
                <div className="StatsDetails">
                    {this.state.category && (
                        <div className="details" onClick={this.props.titleClicked}>
                            <h1>
                                <i className="fa fa-bars mobile" />
                                {this.state.category.name}
                                </h1>
                            <p>{this.state.category.description}</p>
                        </div>
                    )}
                    {this.state.entries.map((e, i) => {
                        return (
                            <div key={e.fighter.id} onClick={() => this.showFighter(e.fighter)}>
                                <StatsEntry rank={i} entry={e}/>
                            </div>
                        )
                    })}
                </div>
                {
                    this.state.selectedFighter !== null &&
                    <FighterDetails fighter={this.state.selectedFighter} onCloseClick={this.closeDetails}/>
                }
            </div>
        );
    }
}