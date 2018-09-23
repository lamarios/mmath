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

        let total = this.props.total;
        return (
            <Div>
                <Tchoo>
                    {this.props.total > 0 && <Svg src={"/train/"+total} />}
                </Tchoo>
            </Div>
        );
    }
}