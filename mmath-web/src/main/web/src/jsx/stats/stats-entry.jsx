import React from 'react';

export default class StatsEntry extends React.Component {


    render() {
        const rank = this.props.rank === 0 ? '(C)' : '#' + this.props.rank;
        const rankClass = this.props.rank === 0 ? 'champ rank' : 'rank';

        const entry = this.props.entry;

        const percentStyle = {width: entry.percent+'%'};
        const fighterPicture ={backgroundImage: 'url('+entry.fighter.picture+')'};
        return (<div className="StatsEntry">
                <div className={rankClass}>
                    {rank} {entry.fighter.name}
                </div>
                <div className="bar">
                    <div className="entry-text">{entry.textToShow}</div>
                    <div className="percent" style={percentStyle}></div>
                    <div className="picture" style={fighterPicture}></div>
                </div>
                {/*<div className="details">{entry.details}</div>*/}
            </div>
        )
    }
}