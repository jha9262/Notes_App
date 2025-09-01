import React, { useState, useEffect } from 'react';
import { Routes, Route, useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

// API base URL - using relative path so it works in production
const API_BASE = '/api';

// Main component that shows all notes and handles CRUD operations
function NotesList() {
  // State for all our notes and form data
  const [notes, setNotes] = useState([]);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [editingId, setEditingId] = useState(null); // Track which note we're editing
  const navigate = useNavigate();

  // Load notes when component first mounts
  useEffect(() => {
    fetchNotes();
  }, []);

  // Get all notes from the backend
  const fetchNotes = async () => {
    try {
      const response = await axios.get(`${API_BASE}/notes`);
      setNotes(response.data);
    } catch (error) {
      console.error('Error fetching notes:', error); // TODO: Show user-friendly error
    }
  };

  // Handle form submission for both create and update
  const handleSubmit = async (e) => {
    e.preventDefault(); // Don't refresh the page
    try {
      if (editingId) {
        // We're editing an existing note
        await axios.put(`${API_BASE}/notes/${editingId}`, { title, content });
        setEditingId(null); // Clear edit mode
      } else {
        // Creating a new note
        await axios.post(`${API_BASE}/notes`, { title, content });
      }
      // Clear the form and refresh the list
      setTitle('');
      setContent('');
      fetchNotes();
    } catch (error) {
      console.error('Error saving note:', error); // Should probably show this to user
    }
  };

  // Fill the form with note data for editing
  const handleEdit = (note) => {
    setTitle(note.title);
    setContent(note.content);
    setEditingId(note.id); // This switches the form to edit mode
  };

  // Delete a note - probably should add confirmation dialog
  const handleDelete = async (id) => {
    try {
      await axios.delete(`${API_BASE}/notes/${id}`);
      fetchNotes(); // Refresh the list
    } catch (error) {
      console.error('Error deleting note:', error);
    }
  };

  // Generate shareable link and copy to clipboard
  const handleShare = async (id) => {
    try {
      const response = await axios.get(`${API_BASE}/notes/${id}/share`);
      navigator.clipboard.writeText(response.data.share_url); // Modern clipboard API
      alert('Share URL copied to clipboard!'); // Could use a nicer toast notification
    } catch (error) {
      console.error('Error sharing note:', error);
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
      <h1>Notes App</h1>
      
      <form onSubmit={handleSubmit} style={{ marginBottom: '30px' }}>
        <input
          type="text"
          placeholder="Note title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
          style={{ width: '100%', padding: '10px', marginBottom: '10px' }}
        />
        <textarea
          placeholder="Note content"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          required
          rows="4"
          style={{ width: '100%', padding: '10px', marginBottom: '10px' }}
        />
        <button type="submit" style={{ padding: '10px 20px' }}>
          {editingId ? 'Update Note' : 'Create Note'}
        </button>
        {editingId && (
          <button 
            type="button" 
            onClick={() => { setEditingId(null); setTitle(''); setContent(''); }}
            style={{ padding: '10px 20px', marginLeft: '10px' }}
          >
            Cancel
          </button>
        )}
      </form>

      <div>
        {notes.map((note) => (
          <div key={note.id} style={{ border: '1px solid #ccc', padding: '15px', marginBottom: '10px' }}>
            <h3>{note.title}</h3>
            <p>{note.content}</p>
            <small>Created: {new Date(note.created_at).toLocaleString()}</small>
            <div style={{ marginTop: '10px' }}>
              <button onClick={() => handleEdit(note)} style={{ marginRight: '10px' }}>Edit</button>
              <button onClick={() => handleDelete(note.id)} style={{ marginRight: '10px' }}>Delete</button>
              <button onClick={() => handleShare(note.id)}>Share</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

function SharedNote() {
  const { shareToken } = useParams();
  const [note, setNote] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchSharedNote = async () => {
      try {
        const response = await axios.get(`${API_BASE}/shared/${shareToken}`);
        setNote(response.data);
      } catch (error) {
        setError(error.response?.data?.detail || 'Note not found');
      }
    };
    fetchSharedNote();
  }, [shareToken]);

  if (error) {
    return <div style={{ padding: '20px', textAlign: 'center' }}>Error: {error}</div>;
  }

  if (!note) {
    return <div style={{ padding: '20px', textAlign: 'center' }}>Loading...</div>;
  }

  return (
    <div style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
      <h1>Shared Note</h1>
      <div style={{ border: '1px solid #ccc', padding: '15px' }}>
        <h2>{note.title}</h2>
        <p>{note.content}</p>
        <small>Created: {new Date(note.created_at).toLocaleString()}</small>
      </div>
    </div>
  );
}

function App() {
  return (
    <Routes>
      <Route path="/" element={<NotesList />} />
      <Route path="/shared/:shareToken" element={<SharedNote />} />
    </Routes>
  );
}

export default App;