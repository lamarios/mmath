import React from 'react';
import styled, {keyframes} from 'styled-components';


const Div = styled.div`
    height:30px;
    width: 100%;
    max-width: 300px;
    position: relative;
`;


const Name = styled.div`
  position: absolute;
  top:0;
  left:0;
`;


const enlarge =  keyframes`
    from{
        width:0;
     }
     
     to{
        width: ${props => (props.count / props.max) * 100}%;
     }
`;


const Bar = styled.div`
  height: 100%;
  background-color: ${props => props.theme.colors.accent};
  animation: ${enlarge} 1s cubic-bezier(0.68, -0.55, 0.265, 1.55);
        width: ${props => (props.count / props.max) * 100}%;
`;


export default class TopTrainBar extends  React.Component{


    constructor(props) {
        super(props);
    };


    render(){
        return (<Div>
            <Name>
                {this.props.train.name} - {this.props.train.count} on board
            </Name>
            <Bar count={this.props.train.count} max={this.props.max}/>
        </Div>);
    }
}