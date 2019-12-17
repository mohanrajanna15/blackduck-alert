import React, { Component } from 'react';
import PropTypes from 'prop-types'
import TableDisplay from 'field/TableDisplay';
import TextInput from 'field/input/TextInput';
import PasswordInput from 'field/input/PasswordInput';
import { connect } from 'react-redux';
import { clearUserFieldErrors, createNewUser, deleteUser, fetchUsers, updateUser } from 'store/actions/users';
import DynamicSelectInput from 'field/input/DynamicSelect';
import { fetchRoles } from 'store/actions/roles';

class UserTable extends Component {
    constructor(props) {
        super(props);

        this.retrieveData = this.retrieveData.bind(this);
        this.createColumns = this.createColumns.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.onSave = this.onSave.bind(this);
        this.onDelete = this.onDelete.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
        this.onConfigClose = this.onConfigClose.bind(this);
        this.createModalFields = this.createModalFields.bind(this);
        this.retrieveRoles = this.retrieveRoles.bind(this);
        this.clearModalFieldState = this.clearModalFieldState.bind(this);

        this.state = {
            user: {},
            roles: []
        };
    }

    createColumns() {
        return [
            {
                header: 'id',
                headerLabel: 'Id',
                isKey: true,
                hidden: true
            },
            {
                header: 'username',
                headerLabel: 'Username',
                isKey: false,
                hidden: false
            },
            {
                header: 'emailAddress',
                headerLabel: 'Email',
                isKey: false,
                hidden: false
            }
        ];
    }

    retrieveData() {
        this.props.getUsers();
    }

    handleChange(e) {
        const { name, value, type, checked } = e.target;
        const { user } = this.state;

        const updatedValue = type === 'checkbox' ? checked.toString().toLowerCase() === 'true' : value;
        const newUser = Object.assign(user, { [name]: updatedValue });
        this.setState({
            user: newUser
        });
    }

    onSave() {
        const { user } = this.state;
        this.props.createUser(user);
        this.retrieveData();
    }

    onUpdate() {
        const { user } = this.state;
        this.props.updateUser(user);
        this.retrieveData();
    }

    onDelete(usersToDelete) {
        if (usersToDelete) {
            usersToDelete.forEach(userId => {
                this.props.deleteUser(userId);
            });
        }
        this.retrieveData();
    }

    onConfigClose() {
        this.props.clearFieldErrors()
    }

    clearModalFieldState() {
        if (this.state.user && Object.keys(this.state.user).length > 0) {
            this.setState({
                user: {}
            });
        }
    }

    retrieveRoles() {
        return this.props.roles.map(role => {
            const rolename = role.roleName;
            return { label: rolename, value: rolename }
        });
    }

    createModalFields(selectedRow) {
        const { user } = this.state;
        const { fieldErrors } = this.props;

        let newUser = user;
        if (selectedRow) {
            newUser = Object.assign({}, selectedRow, user);
            if (user.id !== newUser.id) {
                this.setState({
                    user: newUser
                });
            }
        }

        const usernameKey = 'username';
        const passwordKey = 'password';
        const emailKey = 'emailAddress';
        const roleNames = 'roleNames';
        const passwordSetKey = 'passwordSet';
        return (
            <div>
                <TextInput name={usernameKey} label="Username" description="The users username." required={true} onChange={this.handleChange} value={newUser[usernameKey]} errorName={usernameKey} errorValue={fieldErrors[usernameKey]} />
                <PasswordInput name={passwordKey} label="Password" description="The users password." required={true} onChange={this.handleChange} value={newUser[passwordKey]} isSet={newUser[passwordSetKey]} errorName={passwordKey}
                               errorValue={fieldErrors[passwordKey]} />
                <TextInput name={emailKey} label="Email" description="The users email." required={true} onChange={this.handleChange} value={newUser[emailKey]} errorName={emailKey} errorValue={fieldErrors[emailKey]} />
                <DynamicSelectInput
                    name={roleNames}
                    id={roleNames}
                    label="Roles"
                    description="Select the roles you want associated with the user."
                    onChange={this.handleChange}
                    multiSelect={true}
                    options={this.retrieveRoles()}
                    value={newUser[roleNames]}
                    onFocus={this.props.getRoles} />
            </div>
        );
    }

    render() {
        const { canCreate, canDelete, fieldErrors, userDeleteError } = this.props;
        const fieldErrorKeys = Object.keys(fieldErrors);
        const hasErrors = fieldErrorKeys && fieldErrorKeys.length > 0
        return (
            <div>
                <div>
                    <TableDisplay
                        newConfigFields={this.createModalFields}
                        modalTitle="User"
                        clearModalFieldState={this.clearModalFieldState}
                        onConfigSave={this.onSave}
                        onConfigUpdate={this.onUpdate}
                        onConfigDelete={this.onDelete}
                        onConfigClose={this.onConfigClose}
                        refreshData={this.retrieveData}
                        data={this.props.users}
                        columns={this.createColumns()}
                        newButton={canCreate}
                        deleteButton={canDelete}
                        hasFieldErrors={hasErrors}
                        errorDialogMessage={userDeleteError}
                    />
                </div>
            </div>
        );
    }
}

UserTable.defaultProps = {
    canCreate: true,
    canDelete: true,
    userDeleteError: null,
    fieldErrors: {}
};

UserTable.propTypes = {
    canCreate: PropTypes.bool,
    canDelete: PropTypes.bool,
    userDeleteError: PropTypes.string,
    fieldErrors: PropTypes.object
};

const mapStateToProps = state => ({
    users: state.users.data,
    roles: state.roles.data,
    userDeleteError: state.users.userDeleteError,
    fieldErrors: state.users.fieldErrors
});

const mapDispatchToProps = dispatch => ({
    createUser: user => dispatch(createNewUser(user)),
    updateUser: user => dispatch(updateUser(user)),
    deleteUser: userId => dispatch(deleteUser(userId)),
    getUsers: () => dispatch(fetchUsers()),
    getRoles: () => dispatch(fetchRoles()),
    clearFieldErrors: () => dispatch(clearUserFieldErrors())
});

export default connect(mapStateToProps, mapDispatchToProps)(UserTable);