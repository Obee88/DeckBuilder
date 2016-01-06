"use strict";

import React from 'react';

class PrintToken extends React.Component {
	/**
		props:
			model					[Object]
			onDoubleClickCallback 	function	
	*/

	constructor(){
		super()
	}

	handleDoubleClick(){
		this.props.onDoubleClickCallback(this.props.model);
	}

	render(){
		var className = "print-token";
		return (
			<div className={className} >
				<img style={{width:"60px", height:"80px"}} src={this.props.model.url} 
						onDoubleClick={this.handleDoubleClick.bind(this)} />
			</div>	
		);
	}
}

export default PrintToken