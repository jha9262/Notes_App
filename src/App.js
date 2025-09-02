import React, { useState, useEffect } from 'react';
import { Routes, Route, useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const API_BASE = 'http://localhost:8000/api';

function NotesList() {
  const [notes, setNotes] = useState([]);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [editingId, setEditingId] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchNotes();
  }, []);

  const fetchNotes = async () => {
    try {
      const response = await axios.get(`${API_BASE}/notes`);
      setNotes(response.data);
    } catch (error) {
      console.error('Error fetching notes:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await axios.put(`${API_BASE}/notes/${editingId}`, { title, content });
        setEditingId(null);
      } else {
        await axios.post(`${API_BASE}/notes`, { title, content });
      }
      setTitle('');
      setContent('');
      fetchNotes();
    } catch (error) {
      console.error('Error saving note:', error);
    }
  };

  const handleEdit = (note) => {
    setTitle(note.title);
    setContent(note.content);
    setEditingId(note.id);
  };

  const handleDelete = async (id) => {
    try {
      await axios.delete(`${API_BASE}/notes/${id}`);
      fetchNotes();
    } catch (error) {
      console.error('Error deleting note:', error);
    }
  };

  const handleShare = async (id) => {
    try {
      const response = await axios.get(`${API_BASE}/notes/${id}/share`);
      navigator.clipboard.writeText(response.data.share_url);
      alert('Share URL copied to clipboard!');
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
            <small>Created: {new Date(note.createdAt).toLocaleString()}</small>
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
        <small>Created: {new Date(note.createdAt).toLocaleString()}</small>
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