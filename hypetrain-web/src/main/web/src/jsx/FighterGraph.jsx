import React from 'react';
import styled from 'styled-components';
import Service from './Service';
import Chartist from 'chartist';
import ChartistGraph from 'react-chartist';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faPlus, faMinus} from '@fortawesome/free-solid-svg-icons'

const GraphContainer = styled.div`

  user-select: none;

.ct-label{
  color: ${props => props.theme.colors.text};
}
 .ct-area{
  fill: ${props => props.theme.colors.accent};
  fill-opacity:0.3;
 }
 
 .ct-grid{
 stroke: rgba(255,255,255, 0.5);
 }
 .ct-point, .ct-line{
 stroke: ${props => props.theme.colors.accent};
 }
 
 .ct-label.ct-horizontal{
    writing-mode: vertical-lr;
    padding-top: 5px;
 }
`;

const Title = styled.h2`

`;

const ActionBar = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
`;

const ActionButton = styled(FontAwesomeIcon)`
  display: block;
  padding:3px;
  margin: 2px;
  cursor: pointer;
  color: ${props => props.theme.colors.accent};
`;

export default class FighterGraph extends React.Component {

    constructor(props) {
        super(props);

        this.service = new Service();
        this.state = {stats: {}, count: 5};
        this.refreshGraph = this.refreshGraph.bind(this);
        this.decreaseCount = this.decreaseCount.bind(this);
        this.increaseCount = this.increaseCount.bind(this);
    }


    componentDidMount() {
        this.refreshGraph();
    }

    componentDidUpdate(prevProps) {
        if (this.props.fighter !== prevProps.fighter) {
            this.setState({count: 5}, () => this.refreshGraph());
        }
    }

    decreaseCount() {
        let count = this.state.count - 1;
        if (count < 5) {
            count = 5;
        }

        this.setState({count: count}, () => this.refreshGraph());
    }

    increaseCount() {
        let count = this.state.count + 1;
        this.setState({count: count}, () => this.refreshGraph());
    }


    refreshGraph() {
        this.service.getFighterStats(this.props.fighter, this.state.count)
            .then(stats => {

                let labels = [];
                let values = [];

                Object.keys(stats).forEach(d => {
                    const count = stats[d];

                    labels.push(d);
                    values.push(count);
                });

                this.setState({
                    stats: {
                        labels: labels,
                        series: [values],
                    }
                })
            });

    }


    render() {
        let options = {
            width: '100%',
            height: '300px',
            low: 0,
            showArea: true,
            fullWidth: true,
            lineSmooth: Chartist.Interpolation.none(),
            axisX: {
                offset: 80
            },
            axisY: {
                onlyInteger: true
            }
        };
        console.table(this.state.stats);
        return (
            <GraphContainer>
                <Title>Trend</Title>
                {this.state.stats && <div>
                    <ChartistGraph className={'ct-chart'} data={this.state.stats} type={'Line'} options={options}/>
                    <ActionBar>
                        <ActionButton icon={faMinus} onClick={() => this.decreaseCount()}/>
                        <span>{this.state.count} months</span>
                        <ActionButton icon={faPlus} onClick={() => this.increaseCount()}/>

                    </ActionBar>
                </div>
                }
            </GraphContainer>
        );
    }
}