"use strict";

import React from 'react';

class AddTokenForm extends React.Component {

	constructor(){
		super()
	}

	render(){
		return (
			<table>
				<tr>
					<td>
						name: 
					</td>
					<td>
						<input type="text" />
					</td>
				</tr>
				<tr>
					<td>
						image-url: 
					</td>
					<td>
						<input type="text" />
					</td>
				</tr>
				<tr>
					<td> 
					</td>
					<td>
						<input type="text" value="save"/>
					</td>
				</tr>
			</table>	
		);
	}
}

export default AddTokenForm