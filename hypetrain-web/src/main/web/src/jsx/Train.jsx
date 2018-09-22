import React from 'react';
import styled, {keyframes} from 'styled-components';

import front from '../images/train-main.svg';
import car from '../images/train-wagon.svg';

const animation = keyframes`
  0%{
    transform: translateX(-105%);
  }
  
  90%{
    transform: translateX(calc(0% + 100vw));
  }
  100%{
    transform: translateX(calc(0% + 100vw));
  }
`;

const Div = styled.div`
 height: 40px;  
 position:relative;
 
`;
const Tchoo = styled.div`
  white-space: nowrap;
  position: absolute;
  top:0;
  left:0;
   animation: ${animation} 20s infinite linear;
`;
const Svg = styled.img`
    height: 40px;
    display: inline-block;
`;

export default class Train extends React.Component {


    render() {
        const train = [];

        let total = this.props.total;
        //let's count 20 people / car
        // for (let i = 0; i < this.props.total / 20; i++) {
        for (let i = 0; i < total / 20; i++) {
            train.push(<Svg key={i} src={car}/>);
        }

        train.push(<Svg key={-1} src={front}/>);

        return (
            <Div>
                <Tchoo>
                    {this.props.total > 0 && train}
                </Tchoo>
            </Div>
        );
    }
}