	function checkSession() {
	    fetch("/session/check-session")
	        .then(response => {
				console.log('session check invoked');
	            if (!response.ok) {
					console.log('status', response.status);
	                if (response.status === 401) { // Unauthorized (session expired)
	              //      window.location.replace("/login?sessionExpired=true");
	                    window.location.href = "/login?sessionExpired=true";
	                } else {
	                    throw new Error('Network response was not ok');
	                }
	            }
	  
	        });
	}
	
 let columnMapping = {};
 
	 // Function to get column mappings for a specific file
	function getColumnMapping(fileId) {
	  const data = JSON.parse(sessionStorage.getItem(`columnMappings_${fileId}`));
	  console.log('GET fileId', `columnMappings_${fileId}`);
	  console.log('columnMap in GET:', data);
	  return data ? data : getDefaultMapping();
	}
	
	// Function to save column mappings for a specific file
	function saveColumnMapping(fileId, mappings) {
	  sessionStorage.setItem(`columnMappings_${fileId}`, JSON.stringify(mappings));
	}
 
	function getDefaultMapping() {
	    return {
				'Azonosító': 'identifier',
			    'I_km/h': 'speed1',
			    'II_km/h': 'speed2',
			    'III_km/h': 'speed3',
			    'I_ak_kat_N': 'acousticCatDay1',
			    'II_ak_kat_N': 'acousticCatDay2',
			    'III_ak_kat_N': 'acousticCatDay3',
			    'I_ak_kat_E': 'acousticCatNight1',
			    'II_ak_kat_E': 'acousticCatNight2',
			    'III_ak_kat_E': 'acousticCatNight3',
			    'R_Azonosító': 'identifierR',
			    'R_I_km/h': 'speed1R',
			    'R_II_km/h': 'speed2R',
			    'R_III_km/h': 'speed3R',
			    'R_I_ak_kat_N': 'acousticCatDay1R',
			    'R_II_ak_kat_N': 'acousticCatDay2R',
			    'R_III_ak_kat_N': 'acousticCatDay3R',
			    'R_I_ak_kat_E': 'acousticCatNight1R',
			    'R_II_ak_kat_E': 'acousticCatNight2R',
			    'R_III_ak_kat_E': 'acousticCatNight3R',
			    'LAeq Nappal(ref) [dB]' : 'laeqDay',
			    'LAeq Éjjel(ref) [dB]' : 'laeqNight',
			    'LW Nappal [dB]' : 'lwDay',
			    'LW Éjjel [dB]' : 'lwNight',
			    'Védőtávolság Nappal [m]' : 'protectiveDistanceDay',
			    'Védőtávolság Éjjel [m]' : 'protectiveDistanceNight',
			    'Hatásterület Nappal [m]' : 'impactAreaDay',
			    'Hatásterület Éjjel [m]' : 'impactAreaNight',
			};
		}
		
	const requiredFields = {
	    'I_km/h': 'speed1',
	    'II_km/h': 'speed2',
	    'III_km/h': 'speed3',
	    'I_ak_kat_N': 'acousticCatDay1',
	    'II_ak_kat_N': 'acousticCatDay2',
	    'III_ak_kat_N': 'acousticCatDay3',
	    'I_ak_kat_E': 'acousticCatNight1',
	    'II_ak_kat_E': 'acousticCatNight2',
	    'III_ak_kat_E': 'acousticCatNight3',
	    'R_I_km/h': 'speed1R',
	    'R_II_km/h': 'speed2R',
	    'R_III_km/h': 'speed3R',
	    'R_I_ak_kat_N': 'acousticCatDay1R',
	    'R_II_ak_kat_N': 'acousticCatDay2R',
	    'R_III_ak_kat_N': 'acousticCatDay3R',
	    'R_I_ak_kat_E': 'acousticCatNight1R',
	    'R_II_ak_kat_E': 'acousticCatNight2R',
	    'R_III_ak_kat_E': 'acousticCatNight3R',		
	};
    
		    
	const editableColumns = [
        'identifier',
        'speed1',
        'speed2',
        'speed3',
        'acousticCatDay1',
        'acousticCatDay2',
        'acousticCatDay3',
        'acousticCatNight1',
        'acousticCatNight2',
        'acousticCatNight3',
        'identifierR',
        'speed1R',
        'speed2R',
        'speed3R',
        'acousticCatDay1R',
        'acousticCatDay2R',
        'acousticCatDay3R',
        'acousticCatNight1R',
        'acousticCatNight2R',
        'acousticCatNight3R',
    ];
	        
	const parameterMapping = {
	    'lthDay': 'Határérték Nappal',
	    'lthNight': 'Határérték Éjjel',
	    'roadSurfaceRoughness': 'K (útburkolati érdesség)',
	    'reflection': 'Kr (reflexió)',
	    'soundAbsorptionFactor': 'C (hangelnyelési tényező)',
	    'angleOfView': 'Látószög',
	    'trafficType': 'Forgalom típusa',
	    'slopeElevation': 'Lejtés/emelkedés (%-ban)',
	};
	
	const selectableValues = {
	    'lthDay': [50.0, 55.0, 60.0, 65.0],
	    'lthNight': [40.0, 45.0, 50.0, 55.0],
	    'roadSurfaceRoughness': [0.0, 0.29, 0.49, 0.67, 0.78],
	    'reflection': [0.5, 1.0, 1.5, 2.0, 2.5, 3.5],
	    'soundAbsorptionFactor': [12.5, 15.0],
	    'trafficType' : ['egyenletes', 'lassuló', 'gyorsuló']
	};
	

	const userInputValues = {
		'angleOfView' : true,
		'slopeElevation' : true
	};
	
	const showSpinner = () => {
	    document.getElementById('spinner').style.display = 'block';
	};
	
	// Function to hide the loading indicator
	const hideSpinner = () => {
	    document.getElementById('spinner').style.display = 'none';
	};
	

    let differenceColumnDefault = [0, 1, 2];
    let differenceColumnTemp = [];
    let differenceColumnToUpdate = 0;
    
    let previousDifferenceColumnToUpdate;
	let previousDifferenceColumnTemp;
	let previousDifferenceColumnDefault;
	
	let loading = true;
	
	function handleExcelSave() {
		checkSession();
	//	document.body.style.cursor = "wait"; 
		showSpinner();
		console.log('groupedData[activeFileId]', groupedData[activeFileId]);
		console.log('groupedData[activeFileId][0]', groupedData[activeFileId][0]);
		if (groupedData[activeFileId] === null) {
			return;
		}
		
		const activeFileData = groupedData[activeFileId][0];

	    const columnNames = Object.keys(columnMapping).filter(key => {
		    const dataKey = columnMapping[key];
		    return activeFileData[dataKey] !== null && activeFileData[dataKey] !== undefined;
		});
		
		const fileName = groupedData[activeFileId][0].fileName;
	    
	    console.log('columnNames', columnNames);
		
		fetch(`/console/saveToExcel/${activeFileId}/${fileName}`,{
			method: 'POST',
			headers: {
				'Content-type': 'application/json'
			},
			body: JSON.stringify(columnNames)
		})
		.then(async (response) => {
			if (response.ok) {
	            const blob = await response.blob();
	            const url = window.URL.createObjectURL(blob);
	
	            // Create an anchor element
	            const a = document.createElement('a');
	            a.href = url;
	            a.download = `${fileName}.xlsx`;
	            document.body.appendChild(a);
	            a.click();
	            a.remove();
	            window.URL.revokeObjectURL(url);
	        //    document.body.style.cursor = "auto"; 
	        	hideSpinner();
        	} else {
				const errorMessage = await response.text();
	            console.error('Error during saving the file:', errorMessage);
	            alert(`Hiba: ${errorMessage}`);
	            hideSpinner();
	            throw new Error('Network response was not ok');
			}
		})
		.catch((error) => {
			console.error('Error in saving the file:', error);
			hideSpinner();
		});
		
	}
			
	let deleteMode = false;
	let selectedRows = new Set();
	let selectedColumns = new Set();
			
	// handle Delete Rows and Columns
	function handleDeleteRowsColumns() {
		checkSession();
		const deleteButtonConfirmation = document.getElementById('deleteButtonConfirmation');
		const deleteButton = document.getElementById('deleteButton');
	    const deleteButtonContainer = document.getElementById('deleteRowsColumns');
	    if (deleteMode) {
	        deleteMode = false;
	        deleteButtonContainer.classList.remove('delete-mode-on');
	        deleteButtonContainer.classList.add('delete-mode-off');
	        enableOtherButtons();
	        if (deleteButton) {
	            deleteButton.remove();
	        }
	    } else {
	        deleteMode = true;
	        deleteButtonContainer.classList.add('delete-mode-on');
	        disableOtherButtons();
	        if (!deleteButton) {
				const newDeleteButton = document.createElement('button');
				newDeleteButton.id = 'deleteButton';
            	newDeleteButton.textContent = 'Törlés!';
            	newDeleteButton.className = 'btn btn-dark';
            	newDeleteButton.addEventListener('click', deleteSelected);
            	deleteButtonConfirmation.appendChild(newDeleteButton);
			}
	    }
	}
	
	function disableOtherButtons() {
	    const otherButtons = document.querySelectorAll('#calculations, #submitButton button, #differenceButton button, #text-field-container, #difference-container button, #sorting-container button, #saveFile button, .tabs');
	    otherButtons.forEach(button => {
	        button.disabled = true;
	    });
	}
	
	function enableOtherButtons() {
	    const otherButtons = document.querySelectorAll('#calculations, #submitButton button, #differenceButton button, #text-field-container, #difference-container button, #sorting-container button, #saveFile button, .tabs');
	    otherButtons.forEach(button => {
	        button.disabled = false;
	        const table = document.getElementById('dataTable');
	        clearAllCellBackgrounds(table);
	        selectedRows.clear();
	        selectedColumns.clear();
	        
	    });
	}
	
	// Function to delete selected rows and columns
	function deleteSelected() {
		checkSession();
	//	document.body.style.cursor = "wait"; 
		showSpinner();
        const selectedRowNumbers = Array.from(selectedRows); // Get an array of selected row numbers
        const incrementedRowNumbers = selectedRowNumbers.map(rowNumber => rowNumber + 1);
	    // Ensure selectedColumns contains keys from columnMapping
	    console.log('selectedColumns', selectedColumns);

	    // Convert Set to Array and extract values from columnMapping based on selected column indices
	    const selectedColumnNames = Array.from(selectedColumns).map(columnIndex => {
    		// Retrieve the keys (column names) from columnMapping
		    const keys = Object.keys(columnMapping);
		console.log('keys', keys);
		    // Get the column name using the index from selectedColumns
		    return columnMapping[keys[columnIndex]];
		});
	    
	    // Prepare data to send to the backend
	    const data = {
	        selectedRows: incrementedRowNumbers,
	        selectedColumns: selectedColumnNames,
	    };
	    console.log('incrementedRowNumbers:', incrementedRowNumbers);
	    console.log('deletion-selectedRowNumbers', selectedRowNumbers);
	    console.log('deletion-selectedColumnNames', selectedColumnNames);
	    console.log('deletion-data for backend', data);
	
	    fetch(`/modification/deleteRowsColumns/${activeFileId}`, {
	        method: 'POST',
	        headers: {
	            'Content-Type': 'application/json',
	        },
	        body: JSON.stringify(data),
	    })
	    .then(response => {
	        if (response.ok) {
				deleteColumns(selectedColumnNames);
				handleDeleteRowsColumns();
			//	renderTable(groupedData[activeFileId]);
				fetchDataAndRenderTabs();
			//	document.body.style.cursor = "auto"; 
				hideSpinner();
			} else {
				const errorMessage = response.text();
	            console.error('Error during deleting rows/columns:', errorMessage);
	            alert(`Hiba: ${errorMessage}`);
	            hideSpinner();
	            throw new Error('Network response was not ok');
			}
	    })
	    .catch(error => {
	        // Handle any errors that occur during the fetch
	        console.error('Error:', error);
	        hideSpinner();
	    });
	}
	
	function deleteColumns(selectedColumnNames) {
		checkSession();
		for (const key in columnMapping) {
			if (Object.prototype.hasOwnProperty.call(columnMapping, key)) {
				const value = columnMapping[key];
				if (selectedColumnNames.includes(value)) {
					delete columnMapping[key];
				}
			}
		}
		console.log('columnMapping after column deletion:', columnMapping);
		saveColumnMapping(activeFileId, columnMapping);
	}
	
	const handleRowSelection = (e) => {
		if (deleteMode) {
			const table = document.getElementById('dataTable');
			const clickedCell = e.target.closest('td');
			const rowElement = clickedCell.closest('tr');
        	//const row = Array.from(rowElement.parentNode.children).indexOf(rowElement);
        	
			const isRowData = clickedCell && clickedCell.cellIndex === 0 && e.target.tagName === 'TD';
			console.log('clickedCell in rowSelection', clickedCell);
			if (isRowData) {
				const rowIndex = Array.from(rowElement.parentNode.children).indexOf(rowElement);
	            console.log('rowIndex', rowIndex);
	            if (selectedRows.has(rowIndex)) {
	                selectedRows.delete(rowIndex);
	            } else {
	                selectedRows.add(rowIndex);
	            }
			}
			updateVisuals(table, selectedRows, selectedColumns);
		}
	}
	
	const handleColumnSelection = (e) => {
		if (deleteMode) {
			const table = document.getElementById('dataTable');
			const clickedCell = e.target.closest('th');
			const isColumnHeader = clickedCell && e.target.tagName === 'TH';
			console.log('clickedCell in columnSelection', clickedCell);
		    if (isColumnHeader) {
		        const cellIndex = e.target.cellIndex;
		        if (selectedColumns.has(cellIndex)) {
		            selectedColumns.delete(cellIndex);
		        } else {
		            selectedColumns.add(cellIndex);
		        }
	        }
	        updateVisuals(table, selectedRows, selectedColumns);
        }
	}
	
	// Function to update visuals for both rows and columns
	const updateVisuals = (table, selectedRows, selectedColumns) => {
	    clearAllCellBackgrounds(table);
	    
	    // Update column visuals
	    selectedColumns.forEach(columnIndex => {
	        const cellsInColumn = Array.from(table.querySelectorAll(`th:nth-child(${columnIndex + 1}), td:nth-child(${columnIndex + 1})`));
	        cellsInColumn.forEach(cell => {
	            cell.classList.add('highlighted-cell');
	        });
	    });
	    
	    // Update row visuals
	    const tableBody = table.querySelector('tbody'); 
	    const rows = Array.from(tableBody.children);
	
	    selectedRows.forEach(rowIndex => {
	        const row = rows[rowIndex]; // Access the rows in the <tbody> directly
	        const cellsInRow = Array.from(row.cells);
	        cellsInRow.forEach(cell => {
	            cell.classList.add('highlighted-cell');
	        });
	    });
	};

	// Function to clear all cell background colors
	const clearAllCellBackgrounds = (table) => {
	    const allCells = Array.from(table.querySelectorAll('td, th'));
	    allCells.forEach(cell => {
	        cell.style.backgroundColor = '';
	        cell.classList.remove('highlighted-cell');
	    });
	};
			
	// handle ImpactArea Save
	function handleImpactAreaSave() {
		checkSession();
		//document.body.style.cursor = "wait"; 
		showSpinner();
		if (groupedData[activeFileId] === null) {
			hideSpinner();
			return;
		}
		if (groupedData[activeFileId][0].impactAreaDay === null) {
			alert('Hatásterület Nappal mező nincs kiszámolva!');
			hideSpinner();
			return;
		}
		if (groupedData[activeFileId][0].impactAreaNight === null) {
			alert('Hatásterület Éjjel mező nincs számolva!');
			hideSpinner();
			return;
		}
		
		const fileName = groupedData[activeFileId][0].fileName;
		
		fetch(`/console/saveImpactAreaDistance/${activeFileId}/${fileName}`,{
			method: 'POST',
			headers: {
				'Content-type': 'application/json'
			},
		})
		.then(async (response) => {
			if (response.ok) {
	            const blob = await response.blob();
	            const url = window.URL.createObjectURL(blob);
	
	            // Create an anchor element
	            const a = document.createElement('a');
	            a.href = url;
	            a.download = `${fileName}_hatasterulet.zip`; 
	            document.body.appendChild(a);
	            a.click();
	            a.remove();
	            window.URL.revokeObjectURL(url);
	            //document.body.style.cursor = "auto"; 
	            hideSpinner();
        	} else {
				const errorMessage = await response.text();
	            console.error('Error during saving impactArea:', errorMessage);
	            alert(`Hiba: ${errorMessage}`);
	            hideSpinner();
	            throw new Error('Network response was not ok');
			}
		})
		.catch((error) => {
			console.error('Error in saving the file:', error);
			hideSpinner();
		});		
	}	
	
	// handle ProtectiveDistance Save
	function handleProtectiveDistanceSave() {
		checkSession();
		//document.body.style.cursor = "wait"; 
		showSpinner();
		if (groupedData[activeFileId] === null) {
			hideSpinner();
			return;
		}
		if (groupedData[activeFileId][0].protectiveDistanceDay === null) {
			alert('Védőtávolság Nappal mező nincs kiszámolva!');
			hideSpinner();
			return;
		}
		if (groupedData[activeFileId][0].protectiveDistanceNight === null) {
			alert('Védőtávolság Éjjel mező nincs számolva!');
			hideSpinner();
			return;
		}
		
		const fileName = groupedData[activeFileId][0].fileName;
		
		fetch(`/console/saveProtectiveDistance/${activeFileId}/${fileName}`,{
			method: 'POST',
			headers: {
				'Content-type': 'application/json'
			},
		})
		.then(async (response) => {
			if (response.ok) {
	            const blob = await response.blob();
	            const url = window.URL.createObjectURL(blob);
	
	            // Create an anchor element
	            const a = document.createElement('a');
	            a.href = url;
	            a.download = `${fileName}_vedotav.zip`; 
	            document.body.appendChild(a);
	            a.click();
	            a.remove();
	            window.URL.revokeObjectURL(url);
	            //document.body.style.cursor = "auto"; 
	            hideSpinner();
        	} else {
				const errorMessage = await response.text();
	            console.error('Error during saving protectiveDistance:', errorMessage);
	            alert(`Hiba: ${errorMessage}`);
	            hideSpinner();
	            throw new Error('Network response was not ok');
			}
		})
		.catch((error) => {
			console.error('Error in saving the file:', error);
			hideSpinner();
		});
	}
			
	// handle File Save
	function handleFileSave() {
		checkSession();
		//document.body.style.cursor = "wait"; 
		showSpinner();
		console.log('groupedData[activeFileId]', groupedData[activeFileId]);
		console.log('groupedData[activeFileId][0]', groupedData[activeFileId][0]);
		if (groupedData[activeFileId] === null) {
			return;
		}
		if (groupedData[activeFileId][0].lwDay === null) {
			alert('LW Nappal mező nincs kiszámolva!');
			hideSpinner();
			return;
		}
		if (groupedData[activeFileId][0].lwNight === null) {
			alert('LW Éjjel mező nincs számolva!');
			hideSpinner();
			return;
		}
		
		const activeFileData = groupedData[activeFileId][0];

	    const columnNames = Object.keys(columnMapping).filter(key => {
		    const dataKey = columnMapping[key];
		    return activeFileData[dataKey] !== null && activeFileData[dataKey] !== undefined;
		});
		
		const fileName = groupedData[activeFileId][0].fileName;
	    
	    console.log('columnNames', columnNames);
		
		fetch(`/console/saveFile/${activeFileId}/${fileName}`,{
			method: 'POST',
			headers: {
				'Content-type': 'application/json'
			},
			body: JSON.stringify(columnNames)
		})
		.then(async (response) => {
			if (response.ok) {
	            const blob = await response.blob();
	            const url = window.URL.createObjectURL(blob);
	
	            // Create an anchor element
	            const a = document.createElement('a');
	            a.href = url;
	            a.download = `${fileName}.zip`;
	            document.body.appendChild(a);
	            a.click();
	            a.remove();
	            window.URL.revokeObjectURL(url);
	            //document.body.style.cursor = "auto"; 
	            hideSpinner();
        	} else {
				const errorMessage = await response.text();
	            console.error('Error during saving the file:', errorMessage);
	            alert(`Hiba: ${errorMessage}`);
	            hideSpinner();
	            throw new Error('Network response was not ok');
			}
		})
		.catch((error) => {
			console.error('Error in saving the file:', error);
			hideSpinner();
		});
		
	}
				
			// after modifications let the calculations run again
			let isModificationHappened = false;

			// handle data modifications
			let isCellEditing = false;		
			let singleClickTimer;
					
	const handleCellDoubleClick = async (cell) => {
		checkSession();
		if (!deleteMode) {
		    const activeData = groupedData[activeFileId];
		    const rowElement = cell.closest('tr');
        	const row = Array.from(rowElement.parentNode.children).indexOf(rowElement);
		    
			console.log('double clicked on cell');
			console.log('row', row);
			console.log('isCellEditing', isCellEditing);
			
		    // Fetch data for the form
		    const response = await fetch(`/modification/getMutableParameters/${activeFileId}/${row}`);
		    if (!response.ok) {
				const errorMessage = await response.text();
                console.error('Error occured during mutable params modification:', errorMessage);
                alert(`Hiba: ${errorMessage}`);
	            return;
	        }
		    const data = await response.json();
		    
			console.log('data from getMutableParamaters', data);
			
		    // Create a form with fetched data
		    const form = document.createElement('form');
		    form.classList.add('form-control', 'bg-black', 'text-light');
		    
		
		    // Iterate over the parameterMapping keys and create form fields
		    Object.keys(parameterMapping).forEach((key) => {
		        const label = document.createElement('label');
		        label.style.marginRight = '2%';
		        label.textContent = parameterMapping[key];
		        
				console.log('label - parametermapping', label);
				
		        if (userInputValues[key]) {
		            // Create input fields for userInputValues
		            const input = document.createElement('input');
		            input.name = key;
		            console.log('input - userinputvalue', input);
		            input.classList.add('form-control'); 
		            input.type = 'text';
		            // Set the input value based on the fetched data
		            input.value = data[key] || '';
		
		            // Append the input field to the form
		            label.appendChild(input);
		        } else {
		            // Create select dropdowns for selectableValues
		            const select = document.createElement('select');
		            select.name = key;
		            select.classList.add('form-control');
		            // Populate options for select dropdown based on selectableValues
		            selectableValues[key].forEach((value) => {
		                const option = document.createElement('option');
		                option.value = value;
		                option.textContent = value;
		                // Set the selected value based on the fetched data
		                if (data[key] === value) {
		                    option.selected = true;
		                }
		                select.appendChild(option);
		            });
		
		            // Append the select dropdown to the form
		            label.appendChild(select);
		        }
		
		        // Append the label with input/select to the form
		        form.appendChild(label);
		
		    });
		
		    // Create buttons for submit and cancel
		    const submitButton = document.createElement('button');
		    submitButton.textContent = 'Submit';
		    submitButton.classList.add('btn', 'btn-dark'); 
		    submitButton.style.marginRight = '2%';
		    submitButton.addEventListener('click', () => {
				
		        console.log('before submit');
		        
		        handleSubmit();
		        isCellEditing = false;
		        form.remove();
		        formRow.remove();
		    });
		    
		    const handleSubmit = () => {
				//document.body.style.cursor = "wait"; 
				showSpinner();
			    const formData = {};
			
			    // Iterate through all form elements
			    const formElements = form.elements;
			    for (let i = 0; i < formElements.length; i++) {
			        const element = formElements[i];
			        if (element.tagName === 'INPUT' || element.tagName === 'SELECT') {
			            formData[element.name] = element.value;
			        }
			    }
				console.log('during submit, activeFileId, rowNumber, formData:', activeFileId, row, formData);
				
			    fetch(`/modification/setMutableParameters/${activeFileId}/${row}`, {
			        method: 'PUT',
			        headers: {
			            'Content-Type': 'application/json'
			        },
			        body: JSON.stringify(formData)
				    }).then((response) => {
						if (!response.ok) {
							const errorMessage = response.text();
			                console.error('Error occured during mutable param modification:', errorMessage);
			                alert(`Hiba: ${errorMessage}`);
			                hideSpinner();
			                throw new Error('Network response was not ok');
						}
				        console.log('Mutable Parameters modified for the row.', response);
				        //document.body.style.cursor = "auto"; 
				        hideSpinner();
				    }).catch((error) => {
				        console.error('There was a problem with the fetch operation:', error);
				        hideSpinner();
				    });
			};
	
		    const cancelButton = document.createElement('button');
		    cancelButton.textContent = 'Cancel';
		    cancelButton.classList.add('btn', 'btn-dark'); 
		    cancelButton.addEventListener('click', () => {
		        // Handle cancel action - simply remove the form from the DOM
		        isCellEditing = false;
		        form.remove();
		        formRow.remove();
		    });
	
		    // Append buttons to the form
		    form.appendChild(submitButton);
		    form.appendChild(cancelButton);  
		   
	       // Display the form under the clicked row in the table
		    const tableContainer = document.getElementById('table-container');
		    const dataTable = tableContainer.querySelector('#dataTable');
		    const clickedRow = cell.parentNode;
		
		    // Create a new row and cell to hold the form
		    const formRow = dataTable.insertRow(clickedRow.rowIndex + 1); // Insert new row after the clicked row
		    const formCell = formRow.insertCell(0); // Create a cell in the new row
		    formCell.colSpan = 10; 
		
		    // Append the form to the cell
		    formCell.appendChild(form);
	    }
	};
					
	const handleCellClick = (cell) => {
		checkSession();
		if (!deleteMode) {
		    if (!cell) {
		        return;
		    }
		    
		    if (isCellEditing) {
		        return; // Prevents re-triggering when the cell is already in edit mode
		    }
		
		    isCellEditing = true;
		
		    const activeData = groupedData[activeFileId];
		   // const row = cell.parentNode.rowIndex;
		   	const rowElement = cell.closest('tr');
        	const row = Array.from(rowElement.parentNode.children).indexOf(rowElement);
		    const column = cell.cellIndex;
		
		    const columnNames = Object.values(columnMapping);
		    const columnName = columnNames[column];
		    const cellValue = activeData[row][columnName];
		
			console.log('activeData', activeData);
			console.log('row', row);
			console.log('columnName', columnName);
		    console.log('cellValue in data-modification:', cellValue);
		
		    if (editableColumns.includes(columnName)) {			
		        const input = document.createElement('input');
		        input.value = cellValue;
		        
        		input.addEventListener('input', (event) => {
				    const newValue = event.target.value.trim();
				    if (newValue === '') {
				        // If the new value is empty, revert to the original value
				        input.value = cellValue; // Revert the input value
				    } else if (/^\d*$/.test(newValue)) {
				        // Only allow integer values
				        input.value = newValue; // Update input value
				    } else {
				        // Prevent non-integer values from being entered
				        input.value = ''; // Reset input value
				    }
				});
		        
		        cell.innerHTML = '';
		        cell.appendChild(input);
		        
		        input.focus(); 
		        input.select(); 
		
		        input.addEventListener('blur', () => {
				    const updatedValue = input.value;
				    
				    console.log('Updated Value:', updatedValue);
				    console.log('Cell Value:', cellValue);
				    console.log('Are they different?', String(updatedValue) !== String(cellValue));
				
				    // Check if the value has changed
				    if (String(updatedValue) !== String(cellValue)) {
				        activeData[row][columnName] = updatedValue;
				        cell.innerHTML = updatedValue;
				
				        console.log('activeFileId, row, columnName, value', activeFileId, row, columnName, updatedValue);
				        console.log('activeData in data-modification:', activeData);
				
				        saveCellDataToBackend(activeFileId, row, columnName, updatedValue);
				    } else {
				        // Revert the cell content to the original value when unchanged
				        cell.innerHTML = cellValue;
				    }
				
				    isCellEditing = false; // Reset the editing flag
				});
		        
		        input.addEventListener('keydown', (event) => {
		            if (event.key === 'Enter') {
		                input.blur();
		            }
		        });
		    } else {
		        console.log('Cell not editable');
		        isCellEditing = false; 
		    }
	    }
	};
			
			// Function to send updated data to the backend
			const saveCellDataToBackend = (activeFileId, row, columnName, updatedCellValue) => {
				checkSession();
				//document.body.style.cursor = "wait"; 
				showSpinner();
			    fetch(`/modification/cellValue/${activeFileId}/${row}/${columnName}/${updatedCellValue}`, {
			        method: 'PUT',
			        headers: {
			            'Content-Type': 'application/json',
			        },
			    })
			    .then(response => {
			        if (!response.ok) {
						const errorMessage = response.text();
		                console.error('Error occured during cell modification:', errorMessage);
		                alert(`Hiba: ${errorMessage}`);
		                hideSpinner();
			            throw new Error('Failed to save data to the backend');
			        }
			        const activeData = groupedData[activeFileId];
			        console.log('activeData in saveCellDataToBackend, RESPONSE OK', activeData);
			        isModificationHappened = true;
			        //document.body.style.cursor = "auto";
			        hideSpinner(); 
			    })
			    .catch(error => {
			        console.error('Error while updating data:', error);
			        hideSpinner();
			    });
			};


			// handle sorting request
			function handleSorting() {
				checkSession();
				//document.body.style.cursor = "wait"; 
				showSpinner();
				console.log('groupedData in sorting:', groupedData);
				if (groupedData[activeFileId][0].laeqDay !== null && groupedData[activeFileId][0].laeqNight !== null) {
					fetch(`/sortAndDifferences/sortByLaeq/${activeFileId}`, {
				        headers: {
				            'Content-Type': 'application/json'
				        },
				    })
				    .then(response => {
						if (!response.ok) {
							const errorMessage = response.text();
			                console.error('Error occured during calculation:', errorMessage);
			                alert(`Hiba: ${errorMessage}`);
			                hideSpinner();
			                throw new Error('Network response was not ok');
						}
						return response.json();
					})
				    .then(data => {
				        console.log('Fetched Data after Sorting:', data);
				        renderTable(data);
				        //document.body.style.cursor = "auto"; 
				        hideSpinner();
				    })
				    .catch(error => {
				        console.error('There was a problem with the fetch operation:', error);
				        hideSpinner();
				    });
				} else {
					alert('LAeq éjjel érték szükséges a rendezéshez.')
					hideSpinner();
				}
			}

			let removeDropdownTimeout = null;
			let dropdownDivCreated = false;
			// handle difference request
			function handleDifferenceSubmit() {
				checkSession();
				// Check if the dropdownDiv is already created
			    if (dropdownDivCreated) {
			        // If it's already created, return or display a message to the user
			        console.log('Dropdown form is already active.');
			        return;
			    }
    
				console.log('groupedData in difference:', groupedData);
			    const files = Object.keys(groupedData); // Assuming groupedData contains uploaded file names
			
			    // Only proceed if at least 2 files are available
			    if (files.length >= 2) {
			        const container = document.getElementById('dropdownContainer');
			
			        // Create dropdown elements
			        const dropdown1 = document.createElement('select');
			        const dropdown2 = document.createElement('select');
			        dropdown1.classList.add('form-select', 'me-2', 'ms-5');
			        dropdown2.classList.add('form-select', 'me-2', 'ms-2');
			
			        // Fill dropdowns with file names
			        files.forEach(fileId => {
					    const fileName = groupedData[fileId][0].fileName; // Get the fileName based on the fileId

					    const option2 = document.createElement('option');
					
					    // Use fileName for display and fileId for value
					    option2.text = fileName;
					    option2.value = fileId;
					
					   // dropdown1.appendChild(option1);
					    dropdown2.appendChild(option2);
					});
					
					// Get the active file's fileName using activeFileId
			        const activeFileName = groupedData[activeFileId][0].fileName;
			
			        // Create an unchangeable dropdown 1 for the active file's fileName
			        const option1 = document.createElement('option');
			        option1.text = activeFileName;
			        option1.value = activeFileId;
			        option1.disabled = true;
			        option1.selected = true;
			
			        dropdown1.appendChild(option1);
			
			        // Create OK button
			        const okButton = document.createElement('button');
			        okButton.classList.add('btn', 'btn-dark', 'me-5');
			        okButton.style.marginLeft = '2%';
			        okButton.textContent = 'OK';
			        okButton.onclick = () => {
						clearTimeout(removeDropdownTimeout);
			            const fileId1 = dropdown1.value; // Get the selected fileId from the dropdown
    					const fileId2 = dropdown2.value; 
			            
			            const file1 = groupedData[fileId1];
					    const file2 = groupedData[fileId2];
					
					    // Function to check if laeqDay and laeqNight are null
					    const checkLaeqNull = (file) => {
					        return file.some(entry => entry.laeqDay === null || entry.laeqNight === null);
					    };
					
					    // Check if laeqDay or laeqNight are null in either file
					    if (checkLaeqNull(file1) || checkLaeqNull(file2)) {
					        console.log('Error: laeqDay or laeqNight is null in one of the selected files.');
					        dropdownDiv.parentNode.removeChild(dropdownDiv);
					        dropdownDivCreated = false;
		                    dropdownDiv = null; // Reset dropdownDiv
		                    alert('LAeq nincs kiszámítva az egyik választott fájlban.');
					        return; // Prevent sending data if any null value is found
					    }
						// create new columns for the values
						createNewColumnsForDifferences(fileId1, fileId2);
         
			            // Remove the dropdownDiv when 'OK' is clicked
		                if (dropdownDiv && dropdownDiv.parentNode) {
		                    dropdownDiv.parentNode.removeChild(dropdownDiv);
		                    dropdownDivCreated = false;
		                    dropdownDiv = null; // Reset dropdownDiv
		                }
			        };
			        
			        // Wrap the dropdowns and OK button in a container
				    let dropdownDiv = document.createElement('div');
				    dropdownDiv.classList.add('difference-dropdown-container');
				
				    // Append dropdowns and OK button to the container
				    dropdownDiv.appendChild(dropdown1);
				    dropdownDiv.appendChild(document.createTextNode(' és '));
				    dropdownDiv.appendChild(dropdown2);
				    dropdownDiv.appendChild(document.createTextNode(' különbsége'));
				    dropdownDiv.appendChild(okButton);
				
				    // Append the container to the difference-container
				    container.appendChild(dropdownDiv);

			        dropdownDivCreated = true;
			        
	
				    // Set a timeout to remove the dropdown after 10 seconds
				    removeDropdownTimeout = setTimeout(() => {
				        if (dropdownDiv && dropdownDiv.parentNode) {
				            dropdownDiv.parentNode.removeChild(dropdownDiv);
				            dropdownDivCreated = false;
				            dropdownDiv = null; // Reset dropdownDiv
				        }
				    }, 10000);

			    } else {
					alert('Legalább két fájl szükséges a különbségszámításhoz!')
			        console.log('At least two files are required for comparison.');
			    }
			}

			function createNewColumnsForDifferences(fileId1, fileId2) {
				let fileName1 = groupedData[fileId1][0].fileName;
				let fileName2 = groupedData[fileId2][0].fileName;
				
				if (differenceColumnTemp.length < 3) {
					let newKeyDay = `${fileName1} - ${fileName2} különbsége Nappal [dB]`;
					let newKeyNight = `${fileName1} - ${fileName2} különbsége Éjjel [dB]`;
					
					updateDifferenceColumns();
					
					switch (differenceColumnToUpdate) {
			            case 0:
			                columnMapping[newKeyDay] = 'differenceDay0';
			                columnMapping[newKeyNight] = 'differenceNight0';
			                saveColumnMapping(activeFileId, columnMapping);
			                console.log('Updated column mapping after difference:', columnMapping);		                
			                 sendFilesToBackend(fileId1, fileId2);
			                break;
			            case 1:
			                columnMapping[newKeyDay] = 'differenceDay1';
			                columnMapping[newKeyNight] = 'differenceNight1';
			                saveColumnMapping(activeFileId, columnMapping);
			                console.log('Updated column mapping after difference:', columnMapping);
			                 sendFilesToBackend(fileId1, fileId2);
			                break;
			            case 2:
			                columnMapping[newKeyDay] = 'differenceDay2';
			                columnMapping[newKeyNight] = 'differenceNight2';
			                saveColumnMapping(activeFileId, columnMapping);
			                console.log('Updated column mapping after difference:', columnMapping);
			                 sendFilesToBackend(fileId1, fileId2);
			                break;
			            default:
			                alert('Invalid difference column update.');
			                break;
			        }
					
				} else {
					alert('Maxmimum 3 különbségszámítást lehet végezni! Törölj meglévőt, ha újat szeretnél.');
				}
			}
			
			function updateDifferenceColumns() {
				// Store the previous state before modifying arrays
			    previousDifferenceColumnToUpdate = differenceColumnToUpdate;
			    previousDifferenceColumnTemp = [...differenceColumnTemp];
			    previousDifferenceColumnDefault = [...differenceColumnDefault];
			    
			    if (differenceColumnDefault.length > 0) {
			        // Sort the default column array
			        differenceColumnDefault.sort((a, b) => a - b);
			        
			        // Move the smallest element from default to temp
			        const smallestElement = differenceColumnDefault.shift();
			        differenceColumnTemp.push(smallestElement);
			        
			        // Set the value for updating columns and send to backend
			        differenceColumnToUpdate = smallestElement;

					console.log('differenceColumnToUpdate:', differenceColumnToUpdate);
					console.log('differenceCOlumnDefault', differenceColumnDefault);
					console.log('differenceColumnTemp', differenceColumnTemp);
			    }
			}
			
			function deletePredefinedColumns(fileId1, fileId2) {
			    const fileName1 = groupedData[fileId1][0].fileName;
			    const fileName2 = groupedData[fileId2][0].fileName;
			
			    const newKeyDay = fileName1 + ' - ' + fileName2 + ' különbsége Nappal';
			    const newKeyNight = fileName1 + ' - ' + fileName2 + ' különbsége Éjjel';
			
			    // Check if the columns exist in columnMapping and delete them
			    if (columnMapping[newKeyDay]) {
			        delete columnMapping[newKeyDay];
			    }
			    if (columnMapping[newKeyNight]) {
			        delete columnMapping[newKeyNight];
			    }
			}
			
			function handleBadResponse() {
			    differenceColumnToUpdate = previousDifferenceColumnToUpdate;
			    differenceColumnTemp = previousDifferenceColumnTemp;
			    differenceColumnDefault = previousDifferenceColumnDefault;
			}
			
			function sendFilesToBackend(fileId1, fileId2) {
				checkSession();
				//document.body.style.cursor = "wait"; 
				showSpinner();
				console.log('compare files id to backend:', fileId1, fileId2);
			    fetch(`/sortAndDifferences/differences/${fileId1}/${fileId2}`, {
			        method: 'POST',
			        headers: {
			            'Content-Type': 'application/json'
			        },
					body: JSON.stringify({ differenceColumnToUpdate })
			    })
			    .then(response => {
			        if (!response.ok) {
						// Handle deletion of predefined columns
        				deletePredefinedColumns(fileId1, fileId2);
        				handleBadResponse()
						const errorMessage = response.text();
		                console.error('Error occured during calculation:', errorMessage);
		                alert(`Hiba a számítások során: ${errorMessage}`);
		                hideSpinner();
		                throw new Error('Network response was not ok');
			        }
			        			        
			        fetchDataAndRenderTabs();
			        //document.body.style.cursor = "auto"; 
			        hideSpinner();
			    })
			    .catch(error => {
			        console.error('There was a problem with the fetch operation:', error);
			        hideSpinner();
			    });
			}




		    let selectedValue = '';
		    
		    // listener for calculations div
		    function handleCalculationChange() {
				checkSession();
			    const selectElement = document.getElementById('calculations');
			    const textFieldContainer = document.getElementById('text-field-container');
			    selectedValue = selectElement.value;
			    
			    // Show the text field if the last option is selected, hide otherwise
	            if (selectedValue === '/calculations/givenDistance') {
	                textFieldContainer.style.display = 'block';
	            } else {
	                textFieldContainer.style.display = 'none';
	            }
			}
			 function handleSubmit() {
				 checkSession();
				 //document.body.style.cursor = "wait"; 
				 showSpinner();
				if (selectedValue === '/calculations/givenDistance') {
					const userInput = document.getElementById('userInput').value;
					console.log('userInput for noiseAtGivenDistance:', userInput);
					if (userInput !== '') {					
						// Check and delete the columns if they exist in columnMapping
						function valueExists(mapping, value) {
						    return Object.values(mapping).includes(value);
						}
						
						if (valueExists(columnMapping, 'noiseAtGivenDistanceDay')) {
							console.log('deletion triggerend in columnmapping');
						    deleteMappingByValue(columnMapping, 'noiseAtGivenDistanceDay');
						    console.log('columnMapping after deletion:', columnMapping);
						}
						
						if (valueExists(columnMapping, 'noiseAtGivenDistanceNight')) {
						    deleteMappingByValue(columnMapping, 'noiseAtGivenDistanceNight');
						}
						console.log('columnMapping after check of previous existence', columnMapping);
						// Add the updated columns
						
						
						
						function deleteMappingByValue(mapping, valueToDelete) {
						    const keysToDelete = Object.keys(mapping).filter(key => mapping[key] === valueToDelete);
						
						    keysToDelete.forEach(key => {
						        delete mapping[key];
						    });
						}						
			            // other mappings
			            const existingKeys = Object.keys(columnMapping);
						const indexOfImpactAreaNight = existingKeys.indexOf('Hatásterület Éjjel');
						
						const keysBeforeImpactArea = existingKeys.slice(0, indexOfImpactAreaNight + 1);
						const keysAfterImpactArea = existingKeys.slice(indexOfImpactAreaNight + 1);
						console.log('keysBeforeImpactArea', keysBeforeImpactArea);
						console.log('keysAfterImpactArea', keysAfterImpactArea);
						
						
						// Create a new mapping with the original keys before 'Hatásterület Éjjel'
						const updatedColumnMapping = {};
						
						keysBeforeImpactArea.forEach((key) => {
						    updatedColumnMapping[key] = columnMapping[key];
						    console.log('updatedColumnMapping loop:', updatedColumnMapping);
						});
						console.log('updatedColumnMapping before check:', updatedColumnMapping);
						console.log('columnMapping before check of previous existence', columnMapping);
						
							// Add the remaining columns after the new ones
						keysAfterImpactArea.forEach((key) => {
						    updatedColumnMapping[key] = columnMapping[key];
						});
	
						// Create the new mapping for the two columns to add
						const newKeyDay = `Zajterhelés ${userInput} m távolságon Nappal`;
						const newKeyNight = `Zajterhelés ${userInput} m távolságon Éjjel`;
						updatedColumnMapping[newKeyDay] = 'noiseAtGivenDistanceDay';
						updatedColumnMapping[newKeyNight] = 'noiseAtGivenDistanceNight';
						
						console.log('updatedColumnMapping before remaining map:', updatedColumnMapping);
						
					
						
						console.log('updatedColumnMapping before assigning to columnMapping:', updatedColumnMapping);
			            columnMapping = updatedColumnMapping;
			            saveColumnMapping(activeFileId, columnMapping);
						
						
						columnMapping = getColumnMapping(activeFileId);
				
						// Check if all required fields are selected
						const missingFields = Object.keys(requiredFields).filter((field) => !Object.values(columnMapping).includes(requiredFields[field]));
						
						if (missingFields.length > 0) {
						    // Alert the user about missing fields
						    alert(`Nem lehet elvégezni a számítást! Hiányzó oszlop(ok): ${missingFields.join(', ')}`);
						    //document.body.style.cursor = "auto"; 
						    hideSpinner();
						    return;
						}
												
						// for givenDistance endpoint
						console.log('userInput:', userInput);
						fetch(`${selectedValue}/${activeFileId}/${userInput}`, {
							method: 'POST',
							headers: {
								'Content-Type': 'application/json'
							},
						})
						.then(response => {
							if (!response.ok) {
								const errorMessage = response.text();
				                console.error('Error occured during calculation:', errorMessage);
				                alert(`Hiba a számítások során: ${errorMessage}`);
				                hideSpinner();
				                throw new Error('Network response was not ok');
							}
							fetchDataAndRenderTabs();
							//document.body.style.cursor = "auto"; 
							hideSpinner();
						})
						.catch(error => {
							console.error('There was a problem with the fetch operation:', error);
							hideSpinner();
						});
					} else {
						console.log('User input is empty')
						hideSpinner();
					}
				} else {
					columnMapping = getColumnMapping(activeFileId);
					
					// Check if all required fields are selected
					const missingFields = Object.keys(requiredFields).filter((field) => !Object.values(columnMapping).includes(requiredFields[field]));
					console.log('missingFields in givenDistance: ', missingFields);
					if (missingFields.length > 0) {
					    // Alert the user about missing fields
					    alert(`Nem lehet elvégezni a számítást! Hiányzó oszlop(ok): ${missingFields.join(', ')}`);
					    //document.body.style.cursor = "auto"; 
					    hideSpinner();
					    return;
					}
				    	
					// for other endpoints
					if (selectedValue !== '' && activeFileId) {
						fetch(`${selectedValue}/${activeFileId}`, {
							method: 'POST',
							headers: {
								'Content-Type': 'application/json'
							},
						})
						.then(response => {
							if (!response.ok) {
								const errorMessage = response.text();
				                console.error('Error occured during calculation:', errorMessage);
				                alert(`Hiba a számítások során: ${errorMessage}`);
				                hideSpinner();
								throw new Error('Network response was not ok');
							}
							fetchDataAndRenderTabs();
							//document.body.style.cursor = "auto"; 
							hideSpinner();
						})
						.catch(error => {
							console.error('There was a problem with the fetch operation:', error);
							hideSpinner();
						});
					}
				}
			}
			
			let groupedData;
			// Function to fetch data, group by file_id, and render tabs and tables
			const fetchDataAndRenderTabs = () => {
				checkSession();
			//	document.body.style.cursor = "wait"; 
				showSpinner();
			    fetch('/console/displayData')
			        .then(response => {
			            if (!response.ok) {
			                throw new Error('Network response was not ok');
			            }
			            return response.json();
			        })
			        .then(data => {
			            console.log('Fetched Data:', data);
			            groupedData = data
			            renderTabsAndTables(data);
			       //     document.body.style.cursor = "auto"; 
			       		hideSpinner();
			        })
			        .catch(error => {
			            console.error('There was a problem with the fetch operation:', error);
			            hideSpinner();
			        });
			};


			let activeFileId = null;
			let tbody;
			let thead;
			let tableHeadersBody;
			let tables;
			let file_id;
			// Function to render tabs and tables using grouped data
			const renderTabsAndTables = (groupedData) => {
				showSpinner();
			    console.log('Grouped Data:', groupedData);
			    
			        const tabs = Object.keys(groupedData).map(fileId => {
					file_id = fileId;
			        const fileName = groupedData[fileId][0].fileName;
			        const isActive = fileId === activeFileId;

			        return (	
				            <span key={fileId}>
				                <button className={`btn btn-dark ${isActive ? 'flashing' : ''}`} style={{ marginRight: '1%', marginBottom: '1%' }} onClick={() => {
									activeFileId = fileId; // Set the active file_id
									console.log('activeFileId on tabClick', activeFileId);
									console.log('FileId on tabClick', fileId);
									renderTabsAndTables(groupedData); // Re-render tabs
				                    renderTable(groupedData[fileId]);		                    
				                }}>
				                    {fileName}
				                </button>
				            </span>			  
			        );
			    });
			    // Set activeFileId for the lowest file_id by default
			    const lowestFileId = Math.min(...Object.keys(groupedData));
			    
			    if (activeFileId == null) {
					activeFileId = lowestFileId;
				} 
			    
		
			    ReactDOM.render(			     
				        <>
				        <div className="row pt-2 pb-2">
	                        <div className="col">
	                            {tabs}
	                        </div>
                    	</div>
                    	<div className="row mb-5 table-responsive mx-auto" id="table-container"></div> 
                        </>
				 ,
				    document.getElementById('root')
			    );
			console.log('activeFileId in renderTabsAndTables:', activeFileId);
			    // Display the table for the lowest file_id by default
			  //  const lowestFileId = Math.min(...Object.keys(groupedData));
			  
			    renderTable(groupedData[activeFileId]);
			    hideSpinner();
		//	    document.body.style.cursor = "auto"; 
			};
			
			// Function to render a specific table for a file
			const renderTable = (data) => {	
				columnMapping = getColumnMapping(activeFileId);

			    console.log('Data for Table:', data);
			    console.log('columnMapping before rendering', columnMapping);
			    // Extracting the fileName from the data
    			const fileName = data.length > 0 ? data[0].fileName : '';
    			
    			// Displaying the table name (fileName) above the table
				const tableName = <h2 id="tableName">{fileName}</h2>;
				
 				// Filter out fields that have null values based on columnMapping
			    const nonNullColumns = Object.entries(columnMapping)
			        .filter(([displayName, columnKey]) => {
			            // Check if the column exists in the data and if its value is not null
			            return data.some(item => item[columnKey] !== null && item[columnKey] !== undefined);
			        })
			        .map(([displayName, columnKey]) => {
			            return { displayName, columnKey };
			        });
			
			    const tableHeaders = nonNullColumns.map(({ displayName }, index) => (
			        <th scope="col" key={index}>{displayName}</th>
			    ));
			
			    const tableRows = data.map((dataItem, index) => {
			        return (
			            <tr key={index}>
			                {nonNullColumns.map(({ columnKey }, idx) => {
			                    const cellValue = dataItem[columnKey];
			                    return <td key={idx}>{cellValue}</td>;
			                })}
			            </tr>
			        );
			    });
			
			    const table = (
			        <table id='dataTable' className="table table-dark table-striped table-sm table-hover caption-top">
			        <caption>{tableName}</caption>
			            <thead>
			                <tr>
			                    {tableHeaders}
			                </tr>
			            </thead>
			            <tbody>
			                {tableRows}
			            </tbody>
			        </table>
			    );
			    
			    console.log('Rendering Table:', table);
			    ReactDOM.render(table, document.getElementById('table-container'));
			    
			    tbody = document.querySelector('#dataTable tbody');
				console.log('tbody', tbody);
				
				tableHeadersBody = document.querySelector('#dataTable');
				console.log('tableHeadersBody', tableHeadersBody);
				
				thead = document.querySelector('#dataTable thead');
				
				tbody.addEventListener('click', handleRowSelection);
				thead.addEventListener('click', handleColumnSelection);
				
				// Add event listener for cell selection (mousedown)
    		//	tableHeadersBody.addEventListener('mousedown', handleCellSelection);
					
				// Event listener for table cell clicks
				tbody.addEventListener('click', (e) => {
				    if (!isCellEditing) {
				        // Check for double click within a timeframe (300ms in this example)
				        clearTimeout(singleClickTimer);
				        singleClickTimer = setTimeout(() => {
				            handleCellClick(e.target.closest('td'));
				        }, 300); // Adjust the delay here (in milliseconds)
				    }
				});
				
				// Event listener for table cell double-clicks
				tbody.addEventListener('dblclick', (e) => {
				    clearTimeout(singleClickTimer);
				    if (!isCellEditing) {
				        isCellEditing = true; // Set the flag to true to prevent subsequent single-clicks
				        handleCellDoubleClick(e.target.closest('td'));
				        setTimeout(() => {
				        }, 500);
				    }
				});
			};


			

			
			// Call the function to fetch data and render tabs on page load
			checkSession();
			fetchDataAndRenderTabs();




			
			