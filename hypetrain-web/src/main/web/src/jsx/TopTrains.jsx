import React from 'react';
import styled from 'styled-components';
import Service from './Service';
import TopTrainBar from "./TopTrainBar";


const Div = styled.div`
  grid-area: content;
  text-align: center;
  margin-top:20px;
`;
const Title = styled.h2`

`;

export default class TopTrains extends React.Component {

    constructor(props) {
        super(props);
        this.refreshTopTrains = this.refreshTopTrains.bind(this);
        this.service = new Service();
        this.state = {trains: [], max: 0};
    }


    componentDidMount() {
        this.refreshTopTrains();
    }


    /**
     * Gets the top hype trains
     */
    refreshTopTrains() {
        this.service.getTopTrains()
            .then(res => {
                let max = 0;
                if (res.length > 0) {
                    max = res[0].count;
                }

                this.setState({trains: res, max: max});
            });
    }

    render() {
        return (<Div>
                <Title>Top hype trains</Title>
                {this.state.trains.map(t => {
                    return (<TopTrainBar key={t.fighter} train={t} max={this.state.max}/>)
                })}
            </Div>
        );
    }

}