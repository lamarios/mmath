import React from 'react';

import FighterChip from './fighter-chip.jsx';

export default class FightResult extends React.Component {

    render() {
        return (
            <div className="result">
                {
                    this.props.results.map(
                        function (fighter, i, arr) {
                            var notLast = i < arr.length - 1;
                            return (
                                <div key={fighter.id} className="result-step">
                                    <FighterChip fighter={fighter} small={true} removable={false}/>
                                </div>
                            )
                        })
                }
            </div>
        );
    }
}

