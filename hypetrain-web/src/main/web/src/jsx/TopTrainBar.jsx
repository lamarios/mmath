import React from 'react';
import styled, {keyframes} from 'styled-components';
import { NavLink } from 'react-router-dom';

const Div = styled(NavLink)`
    height:30px;
    width: 100%;
    position: relative;
    display:block;
    text-decoration: none;
    color: ${props=>props.theme.colors.text};
    margin:10px auto; 
    &:visited {
      color: ${props=>props.theme.colors.text};
    }
`;


const Name = styled.div`
  position: absolute;
  top:3px;
  left:5px;
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
  //animation: ${enlarge} 1s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  animation: ${enlarge} 1s ease-in-out;
        width: ${props => (props.count / props.max) * 100}%;
`;


export default class TopTrainBar extends  React.Component{


    constructor(props) {
        super(props);
    };


    render(){
        return (<Div to={"/fighter/"+this.props.train.fighter}>
            <Name>
                {this.props.train.name} - {this.props.train.count} on board
            </Name>
            <Bar count={this.props.train.count} max={this.props.max}/>
        </Div>);
    }
}