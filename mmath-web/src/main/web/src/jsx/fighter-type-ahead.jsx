import React from 'react';

export default class FighterTypeAhead extends React.Component {
    render() {
        return (
            <ul className="fighters">
                {

                    this.props.fighters.map(
                        function (fighter) {

                            return <li onClick={this.props.onClick.bind(null, fighter)}
                                       key={fighter.id}
                                       data-id={fighter.id}>
                                {fighter.name}</li>
                        }.bind(this))

                }
            </ul>
        );
    }
}
