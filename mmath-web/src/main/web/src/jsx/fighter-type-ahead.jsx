import React from 'react';

export default class FighterTypeAhead extends React.Component {
    render() {
        return (
            <ul className="fighters">
                {

                    this.props.fighters.map(
                        function (fighter) {
                            var style = {
                                backgroundImage: 'url(' + fighter.picture + ')'
                            };

                            return <li onClick={this.props.onClick.bind(null, fighter)}
                                       key={fighter.id}
                                       data-id={fighter.id}>
                                <div className="icon" style={style}></div>
                                {fighter.name}</li>
                        }.bind(this))

                }
            </ul>
        );
    }
}
